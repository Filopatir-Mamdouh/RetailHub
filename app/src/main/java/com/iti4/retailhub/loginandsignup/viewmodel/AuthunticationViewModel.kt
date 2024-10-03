package com.iti4.retailhub.loginandsignup.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.iti4.retailhub.userlocalprofiledata.UserLocalProfileData
import com.iti4.retailhub.userauthuntication.AuthState
import com.iti4.retailhub.userauthuntication.UserAuthuntication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class UserAuthunticationViewModelViewModel(app: Application) : AndroidViewModel(app) {
    val authRepository = UserAuthuntication(app.applicationContext)
    val serLocalProfileData= UserLocalProfileData.getInstance(app.applicationContext)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Messages("Idle"))
    val authState: StateFlow<AuthState> = _authState
    private val _loginState = MutableStateFlow<String>("")
    val loginState: StateFlow<String> = _loginState


    fun createUser(userName:String,email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.createUserWithEmailAndPassword(email, password)
                if(user?.user != null) {
                    val emailSent = authRepository.sendEmailVerification(user.user!!)
                    if (emailSent) {
                        serLocalProfileData.addUserName(userName)
                        Log.d("UserLocalProfileData", serLocalProfileData.getUserProfileData())
                        _authState.value = AuthState.Messages("Verification email sent")
                    } else {
                        _authState.value = AuthState.Messages("Failed to send verification email")
                    }
                } else {
                    _authState.value = AuthState.Messages("User creation failed")
                }
            } catch (e: FirebaseAuthWeakPasswordException) {
                _authState.value =AuthState.Messages("Password is too weak")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value =AuthState.Messages("Invalid email format")
            } catch (e: FirebaseAuthUserCollisionException) {
                _authState.value =AuthState.Messages("Email is already in use")
            } catch (e: Exception) {
                _authState.value =AuthState.Messages("Error: ${e.message}")
            }
        }
    }
    fun signIn(email: String, password: String) {

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signInWithEmailAndPassword(email, password)
                if (user?.user?.isEmailVerified == true){
                    serLocalProfileData.addUserData(user.user!!.uid,user.user?.displayName.toString(),user.user?.email.toString(),user.user?.photoUrl.toString())
                    Log.d("UserLocalProfileData", serLocalProfileData.getUserProfileData())
                    _authState.value = AuthState.Success(user.user)
                }else{
                    _authState.value = AuthState.Messages("Email is not verified.")
                }
            }catch (e: FirebaseAuthInvalidCredentialsException){
                _authState.value = AuthState.Messages("email or password is incorrect.")
            }catch (e: FirebaseAuthInvalidUserException){
                _authState.value = AuthState.Messages("Email account does not exist")
            } catch (e: Exception) {
                _authState.value = AuthState.Messages("Error: ${e.message}")
            }
        }
    }
    fun signInWithGoogle() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val intentSender = authRepository.signIn()
            if (intentSender != null) {
                _authState.value = AuthState.SignInIntent(intentSender)
            } else {
                _authState.value = AuthState.Messages("Unable to start Google Sign-In.")
            }
        }
    }
     fun handleSignInResult(intent: Intent){
        viewModelScope.launch {
            try {
                val user = authRepository.signInWithIntent(intent)
                if (user != null) {
                    serLocalProfileData.addUserData(user.user!!.uid,user.user?.displayName.toString(),user.user?.email.toString(),user.user?.photoUrl.toString())
                    Log.d("UserLocalProfileData", serLocalProfileData.getUserProfileData())
                    _authState.value = AuthState.Success(user.user)
                }else{
                    _authState.value = AuthState.Messages("Unable to sign in with Google.")
                }
            }catch (e: ApiException){
                _authState.value = AuthState.Messages("Error: ${e.message}")
            }catch (e: FirebaseAuthException){
                _authState.value = AuthState.Messages("Error: ${e.message}")
            }catch (e: Exception){
                _authState.value = AuthState.Messages("Error: ${e.message}")
            }
        }
    }
    fun signOut() {
        viewModelScope.launch {
            val result = authRepository.loginOut()
          if (!result) {
              _loginState.value = "Sign-out failed"
          }else{
              serLocalProfileData.deleteUserData()
              Log.d("UserLocalProfileData", serLocalProfileData.getUserProfileData())
              _loginState.value = "Sign-out successful"
          }
            }
        }
}