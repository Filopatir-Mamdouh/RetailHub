package com.iti4.retailhub.userauthuntication

import android.content.Context

interface UserAuthunticationInterface {
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResultState
     fun loginOut()
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResultState
}
