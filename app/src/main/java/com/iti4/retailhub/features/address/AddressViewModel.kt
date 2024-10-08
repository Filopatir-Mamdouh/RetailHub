package com.iti4.retailhub.features.address

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CustomerAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddressViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val dispatcher = Dispatchers.IO


    private val _addressesState = MutableStateFlow<ApiState>(ApiState.Loading)
    val addressState = _addressesState.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    private val _updatedAddressState = MutableStateFlow<ApiState>(ApiState.Loading)
    val updatedAddressState = _updatedAddressState.asStateFlow()


    private val _updatedAddressLeavingState = MutableStateFlow<ApiState>(ApiState.Loading)
    val updatedAddressLeavingState = _updatedAddressLeavingState.asStateFlow()

    private val _addressLookUp = MutableStateFlow<ApiState>(ApiState.Loading)
    val addressLookUp = _addressLookUp.asStateFlow()

    val customerId by lazy { repository.getUserShopLocalId() }


    fun getAddressesById() {
        viewModelScope.launch(dispatcher) {
            repository.getAddressesById(customerId!!)
                .catch { e -> _addressesState.emit(ApiState.Error(e)) }.collect {
                    _addressesState.emit(ApiState.Success(it))
                }
        }
    }

    fun addAddress(address: CustomerAddress) {
        viewModelScope.launch(dispatcher) {
            _updatedAddressState.emit(ApiState.Success(address))
        }
    }

    fun updateMyAddresses(address: List<CustomerAddress>) {
        GlobalScope.launch(dispatcher) {
            repository.updateCustomerAddress(customerId!!, address)
                .catch { e -> _updatedAddressLeavingState.emit(ApiState.Error(e)) }.collect {
                    _updatedAddressLeavingState.emit(ApiState.Success(it))
                }
        }
    }

    fun getLocationSuggestions(query: String) {
        viewModelScope.launch(dispatcher) {
            repository.getLocationSuggestions(query)
                .catch { e ->
                    _addressLookUp.emit(ApiState.Error(e))
                }
                .collect {
                    if (it.isSuccessful && it.body() != null) {
                        _addressLookUp.emit(ApiState.Success(it.body()))
                    } else {
                        _addressLookUp.emit(ApiState.Error(Exception("error")))
                    }
                }
        }
    }


}