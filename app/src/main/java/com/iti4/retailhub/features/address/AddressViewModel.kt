package com.iti4.retailhub.features.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CustomerAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddressViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val dispatcher = Dispatchers.IO
    var addressesList: MutableList<CustomerAddress> = mutableListOf()
    var selectedMapAddress: PlaceLocation? = null
    var runOnce = true
    private val _addressesState = MutableStateFlow<ApiState>(ApiState.Loading)
    val addressState = _addressesState.asStateFlow().onStart { getAddressesById() }

    private val _editAddressState = MutableStateFlow<ApiState>(ApiState.Loading)
    val editAddressState = _editAddressState.asStateFlow()


    private val _updatedAddressLeavingState = MutableStateFlow<ApiState>(ApiState.Loading)
    val updatedAddressLeavingState = _updatedAddressLeavingState.asStateFlow()


    private val _defaultAddressState = MutableStateFlow<ApiState>(ApiState.Loading)
    val defaultAddressState = _defaultAddressState.asStateFlow()


    private val _addressLookUp = MutableStateFlow<ApiState>(ApiState.Loading)
    val addressLookUp = _addressLookUp.asStateFlow()

    private val _addressGeocoding = MutableStateFlow<ApiState>(ApiState.Loading)
    val addressGeocoding = _addressGeocoding.asStateFlow()


    val customerId by lazy { repository.getUserShopLocalId() }


    fun getAddressesById() {
        viewModelScope.launch(dispatcher) {
            repository.getAddressesById(customerId!!)
                .catch { e -> _addressesState.emit(ApiState.Error(e)) }.collect {
                    _addressesState.emit(ApiState.Success(it))
                }
        }
    }
    fun getDefaultAddress() {
        viewModelScope.launch(dispatcher) {
            repository.getDefaultAddress(customerId!!)
                .catch { e -> _defaultAddressState.emit(ApiState.Error(e)) }.collect {
                    _defaultAddressState.emit(ApiState.Success(it))
                }
        }
    }

    fun addAddress(address: CustomerAddress) {
        if (address.newAddress) {
            addressesList.add(address)
        } else {
            val index = addressesList.indexOfFirst { it.id == address.id }
            addressesList[index] = address
        }
        updateMyAddresses(addressesList)
        viewModelScope.launch(dispatcher) {
            _editAddressState.emit(ApiState.Success(address))
        }
    }

    fun updateMyAddresses(address: List<CustomerAddress>) {
        GlobalScope.launch(dispatcher) {
            repository.updateCustomerAddress(customerId!!, address)
                .catch { e -> _updatedAddressLeavingState.emit(ApiState.Error(e)) }.collect {
                }
        }
    }

    fun updateCustomerDefaultAddress(addressId: String) {
        GlobalScope.launch(dispatcher) {
            repository.updateCustomerDefaultAddress(customerId!!, addressId)
                .collect {
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

    fun getLocationGeocoding(lat: String, lon: String) {
        viewModelScope.launch(dispatcher) {
            repository.getLocationGeocoding(lat, lon)
                .catch { e ->
                    _addressGeocoding.emit(ApiState.Error(e))
                }
                .collect {
                    if (it.isSuccessful && it.body() != null) {
                        _addressGeocoding.emit(ApiState.Success(it.body()))
                    } else {
                        _addressGeocoding.emit(ApiState.Error(Exception("error")))
                    }
                }
        }
    }


}