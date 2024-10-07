package com.iti4.retailhub.features.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.logic.extractNumbersFromString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddressViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _addressesState = MutableStateFlow<ApiState>(ApiState.Loading)
    val addressState = _addressesState.onStart { getAddressesById()}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)
    val customerId by lazy { repository.getUserShopLocalId() }



    private fun getAddressesById() {
        viewModelScope.launch(dispatcher) {
            repository.getAddressesById(customerId!!)
                .catch { e -> _addressesState.emit(ApiState.Error(e)) }.collect {
                    _addressesState.emit(ApiState.Success(it))
                }
        }
    }




}