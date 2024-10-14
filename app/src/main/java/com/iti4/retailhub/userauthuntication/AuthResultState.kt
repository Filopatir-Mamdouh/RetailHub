package com.iti4.retailhub.userauthuntication

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Messages(val error: String) : AuthState()
    data class Error(val exception:Exception):AuthState()
}
