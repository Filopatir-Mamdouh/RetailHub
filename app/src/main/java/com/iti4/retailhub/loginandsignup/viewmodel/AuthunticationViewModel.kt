package com.iti4.retailhub.loginandsignup.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.iti4.retailhub.datastorage.Repository
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.userauthuntication.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserAuthunticationViewModelViewModel @Inject constructor(private val reposatory: Repository) :
    ViewModel() {


    private val _authState = MutableStateFlow<AuthState>(AuthState.Messages("Idle"))
    val authState: StateFlow<AuthState> = _authState
    private val _loginState = MutableStateFlow<String>("")
    val loginState: StateFlow<String> = _loginState


    fun createUser(userName: String, email: String, password: String) {
        val name = userName.split(" ")
        val firstName = name[0]
        val lastName = if (name.size > 1) name[1] else ""
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = reposatory.createUserWithEmailAndPassword(email, password)
                if (user?.user != null) {
                    val emailSent = reposatory.sendEmailVerification(user.user!!)
                    if (emailSent) {
                        reposatory.createUser(
                            CustomerInput(
                                firstName = Optional.present(firstName),
                                lastName = Optional.present(lastName),
                                email = Optional.Present(user.user?.email)
                            )
                        ).catch { e ->
                            _authState.emit(AuthState.Messages(e.message!!))
                            Log.d("shopify", "onViewCreated: ${e.message}")
                        }.collect {
                            Log.d("shopify", "onViewCreated: ${it}")
                            reposatory.addUserShopLocalId(it.customer?.id)
                            Log.d("shopify", reposatory.getUserProfileData())
                            _authState.emit(AuthState.Messages("Verification email sent"))
                            Log.d("shopify", "onViewCreated: ${reposatory.getUserShopLocalId()}")
                        }


                    } else {
                        _authState.value = AuthState.Messages("Failed to send verification email")
                    }
                } else {
                    _authState.value = AuthState.Messages("User creation failed")
                }
            } catch (e: FirebaseAuthWeakPasswordException) {
                _authState.value = AuthState.Messages("Password is too weak")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Messages("Invalid email format")
            } catch (e: FirebaseAuthUserCollisionException) {
                _authState.value = AuthState.Messages("Email is already in use")
            } catch (e: Exception) {
                _authState.value = AuthState.Messages("Error: ${e.message}")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = reposatory.signInWithEmailAndPassword(email, password)
                if (user?.user?.isEmailVerified == true) {
                    reposatory.addUserData(user.user!!.uid)
                    Log.d("UserLocalProfileData", reposatory.getUserProfileData())





                    reposatory.getCustomerIdByEmail(email).catch { e ->
                        _authState.emit(AuthState.Messages(e.message!!))
                        Log.d("shopify", "onViewCreated: ${e.message}")
                    }.collect {
                        Log.d("shopify", "onViewCreated: ${it}")
                        reposatory.addUserShopLocalId(it.edges[0].node.id)
                        _authState.value = AuthState.Success(user.user)
                    }


                } else {
                    _authState.value = AuthState.Messages("Email is not verified")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Messages("email or password is incorrect.")
            } catch (e: FirebaseAuthInvalidUserException) {
                _authState.value = AuthState.Messages("Email account does not exist")
            } catch (e: Exception) {
                _authState.value = AuthState.Messages("Error: ${e.message}")
            }
        }
    }

    fun signInWithGoogle() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val intentSender = reposatory.signIn()
            if (intentSender != null) {
                _authState.value = AuthState.SignInIntent(intentSender)
            } else {
                _authState.value = AuthState.Messages("Unable to start Google Sign-In.")
            }
        }
    }

    fun handleSignInResult(intent: Intent) {
        viewModelScope.launch {
            try {
                val user = reposatory.signInWithIntent(intent)
                if (user != null) {
                    reposatory.addUserData(user.user!!.uid)
                    Log.d("UserLocalProfileData", reposatory.getUserProfileData())


                    reposatory.getCustomerIdByEmail(user.user?.email.toString()).catch { e ->
//                        _authState.emit(AuthState.Messages(e.message!!))
                        Log.d("shopify", "getCustomerIdByEmail: ${e.message}")
                    }.collect {
                        Log.d("shopify", "onViewCreated: ${it}")
                        if (it.edges.isEmpty()) {
                            reposatory.createUser(
                                CustomerInput(
                                    firstName = Optional.present(user.user?.displayName),
                                    email = Optional.Present(user.user?.email)
                                )
                            ).catch { e ->
                                _authState.emit(AuthState.Messages(e.message!!))
                                Log.d("shopify", "catch: ${e.message}")
                            }.collect {
                                Log.d("shopify", "onViewCreated: ${it}")
                                reposatory.addUserShopLocalId(it.customer?.id)
                                _authState.value = AuthState.Success(user.user)
                            }
                        } else {
                            reposatory.addUserShopLocalId(it.edges[0].node.id)
                            _authState.value = AuthState.Success(user.user)
                        }
                    }


                } else {
                    _authState.value = AuthState.Messages("Unable to sign in with Google.")
                }
            } catch (e: ApiException) {
                _authState.value = AuthState.Messages("Error: ${e.message}")
            } catch (e: FirebaseAuthException) {
                _authState.value = AuthState.Messages("Error: ${e.message}")
            } catch (e: Exception) {
                _authState.value = AuthState.Messages("Error: ${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            val result = reposatory.loginOut()
            if (!result) {
                _loginState.value = "Sign-out failed"
            } else {
                reposatory.deleteUserData()
                Log.d("UserLocalProfileData", reposatory.getUserProfileData())
                _loginState.value = "Sign-out successful"
            }
        }
    }
}