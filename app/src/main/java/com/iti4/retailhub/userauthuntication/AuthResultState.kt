package com.iti4.retailhub.userauthuntication

import com.google.firebase.auth.AuthResult

sealed class AuthResultState {
    data class Success(val authResult: AuthResult) : AuthResultState()
    data class Failure(val errorMessage: String) : AuthResultState()
    object Loading : AuthResultState()
}
