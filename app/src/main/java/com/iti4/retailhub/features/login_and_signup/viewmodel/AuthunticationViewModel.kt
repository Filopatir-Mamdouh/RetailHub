package com.iti4.retailhub.features.login_and_signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.userauthuntication.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserAuthunticationViewModel @Inject constructor(private val reposatory: IRepository) :ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Messages("Idle"))
    val authState: StateFlow<AuthState> = _authState



    fun createUserWithEmailAndPassword(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.emit( AuthState.Loading)
            try {
                val user = reposatory.createUserWithEmailAndPassword(email, password)
                if (user?.user != null) {

                    val emailSent = reposatory.sendEmailVerification(user.user!!)

                    if (emailSent) {

                        shopifyCreateUser(firstName,lastName, user.user!!.email)

                    } else {
                        _authState.emit(AuthState.Messages("Failed to send verification email"))
                    }
                } else {
                    _authState.emit(AuthState.Messages("User creation failed"))
                }
            } catch (e: Exception) {
                _authState.emit( AuthState.Error(e))
            }
        }
    }
    /*
    * fun createUser(firstName: String,lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.emit( AuthState.Loading)
            try {
                val user = reposatory.createUserWithEmailAndPassword(email, password)
                if (user?.user != null) {

                    val emailSent = reposatory.sendEmailVerification(user.user!!)

                    if (emailSent) {

                        shopifyCreateUser(firstName,lastName, user.user!!.email)

                    } else {
                        _authState.emit(AuthState.Messages("Failed to send verification email"))
                    }
                } else {
                    _authState.emit(AuthState.Messages("User creation failed"))
                }
            } catch (e: Exception) {
                _authState.emit( AuthState.Error(e))
            }
        }
    }*/

    private suspend fun shopifyCreateUser(firstName: String, lastName: String, email: String?) {
        reposatory.createUser(
            CustomerInput(
                firstName = Optional.present(firstName),
                lastName = Optional.present(lastName),
                email = Optional.Present(email)
            )
        ).catch { e ->
            _authState.emit(AuthState.Messages(e.message!!))
        }.collect {
            reposatory.addUserShopLocalId(it.customer?.id)
            _authState.emit(AuthState.Messages("Verification email sent"))
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.emit(AuthState.Loading)
            try {
                val user = reposatory.signInWithEmailAndPassword(email, password)
                if (user?.user?.isEmailVerified == true) {
                    reposatory.addUserData(user.user!!.uid)
                    reposatory.getCustomerIdByEmail(email).catch { e ->
                        _authState.emit(AuthState.Messages(e.message!!))
                    }.collect {
                        reposatory.addUserShopLocalId(it.edges[0].node.id)
                        _authState.emit( AuthState.Success(user.user))
                    }
                } else {
                    _authState.emit(AuthState.Messages("Email is not verified"))
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.emit( AuthState.Messages("email or password is incorrect."))
            } catch (e: FirebaseAuthInvalidUserException) {
                _authState.emit( AuthState.Messages("Email account does not exist"))
            } catch (e: Exception) {
                _authState.emit( AuthState.Messages("Error: ${e.message}"))
            }
        }
    }

    fun setLoginStatus(loginStatus: String) {
        reposatory.setLoginStatus(loginStatus)
    }

    fun isguestMode(): Boolean {
        if( reposatory.getLoginStatus()=="guest"){
            return true
        }else{
            return false
        }
    }

    fun isUserLoggedIn(): Boolean {
       if( reposatory.getLoginStatus()!=null){
           return true
       }else{
           return false
       }
    }

    fun signWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.emit( AuthState.Loading)
            try {
                val user = reposatory.signWithGoogle(idToken)
                if (user != null) {
                    reposatory.addUserData(user.uid)
                    reposatory.getCustomerIdByEmail(user.email.toString()).catch { e ->
                    }.collect {
                        if (it.edges.isEmpty()) {
                            reposatory.createUser(
                                CustomerInput(
                                    firstName = Optional.present(user.displayName),
                                    email = Optional.Present(user.email)
                                )
                            ).catch { e ->
                                _authState.emit(AuthState.Messages(e.message!!))
                            }.collect {
                                reposatory.setLoginStatus("login")
                                reposatory.addUserShopLocalId(it.customer?.id)
                                _authState.emit(AuthState.Success(user))
                            }
                        } else {
                            reposatory.addUserShopLocalId(it.edges[0].node.id)
                            _authState.emit(AuthState.Success(user))
                        }
                    }
                } else {
                    _authState.emit( AuthState.Messages("Google sign-in failed"))
                }
            } catch (e: Exception) {
                _authState.emit( AuthState.Messages("Error: ${e.message}"))
            }
        }
    }

}