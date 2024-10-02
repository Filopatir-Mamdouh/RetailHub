package com.iti4.retailhub.userauthuntication

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class UserAuthuntication private constructor() : UserAuthunticationInterface {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResultState {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            AuthResultState.Success(result) // Return success state
        } catch (e: FirebaseAuthWeakPasswordException) {
            AuthResultState.Failure("Password is too weak.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResultState.Failure("Invalid email format.")
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResultState.Failure("Email is already in use.")
        } catch (e: Exception) {
            AuthResultState.Failure("Error: ${e.message}")
        }
    }
    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResultState {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResultState.Success(result) // Return success state
        } catch (e:FirebaseAuthInvalidCredentialsException){
            AuthResultState.Failure("email or password is incorrect.")
        }catch (e: FirebaseAuthInvalidUserException){
            AuthResultState.Failure("Email account does not exist")
        } catch (e: Exception) {
            AuthResultState.Failure("Error: ${e.message}")
        }
    }

    override  fun loginOut(){
         auth.signOut()
    }


    companion object {
        @get:Synchronized
        var instance: UserAuthuntication? = null
            get() {
                if (field == null) {
                    field = UserAuthuntication()
                }
                return field
            }
            private set
    }
}

