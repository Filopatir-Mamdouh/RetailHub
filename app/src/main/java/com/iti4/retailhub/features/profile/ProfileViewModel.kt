package com.iti4.retailhub.features.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.models.CountryCodes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val _user = MutableSharedFlow<Map<String,String?>>()
    val user = _user.onStart { getCustomer() }
    fun setCurrencyCode(currencyCode: CountryCodes) {
        repository.setCurrencyCode(currencyCode)
    }

    fun getCurrencyCode(): CountryCodes {
        return repository.getCurrencyCode()
    }

    fun logout(){
        viewModelScope.launch {
            repository.setLoginStatus("")
            repository.loginOut()
        }
    }
    fun getCustomer() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCustomerInfoById(repository.getUserShopLocalId()!!).single().apply {
                val map = mapOf("fName" to firstName, "lName" to lastName, "email" to email)
                Log.d("Filo", "getCustomer: $map")
                _user.emit(map)
            }
        }
    }
}