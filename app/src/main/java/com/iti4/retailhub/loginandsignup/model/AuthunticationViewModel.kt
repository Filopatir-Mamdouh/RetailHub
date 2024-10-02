package com.iti4.retailhub.loginandsignup.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.userauthuntication.AuthResultState
import com.iti4.retailhub.userauthuntication.UserAuthuntication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class UserAuthunticationViewModelViewModel() : ViewModel() {
    val authRepository = UserAuthuntication.instance!!

    private val _authStateFlow = MutableStateFlow<AuthResultState>(AuthResultState.Failure(""))
    val authStateFlow: StateFlow<AuthResultState> = _authStateFlow

    fun createUser(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        _authStateFlow.value = AuthResultState.Loading
        _authStateFlow.value = authRepository.createUserWithEmailAndPassword(email, password)
    }
fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
    _authStateFlow.value = AuthResultState.Loading
    _authStateFlow.value = authRepository.signInWithEmailAndPassword(email, password)
}
    fun loginOut():Boolean {
        authRepository.loginOut()
        return true
    }
}