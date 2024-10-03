package com.iti4.retailhub.userauthuntication

import android.content.Intent
import android.content.IntentSender
import com.google.firebase.auth.AuthResult

interface UserAuthunticationInterface {
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult?
    suspend fun loginOut(): Boolean
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult?
    suspend fun signInWithIntent(intent: Intent): AuthResult?
    suspend fun signIn(): IntentSender?
}
