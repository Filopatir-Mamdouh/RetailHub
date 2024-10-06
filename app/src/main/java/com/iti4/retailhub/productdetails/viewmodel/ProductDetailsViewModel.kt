package com.iti4.retailhub.productdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
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
class ProductDetailsViewModel @Inject constructor(private val repository: IRepository): ViewModel() {
    private val _productDetails = MutableStateFlow<ApiState>(ApiState.Loading)
    val productDetails = _productDetails
//        _productDetails.onStart { getProductDetails() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

     fun getProductDetails(id:String) {
        viewModelScope.launch(Dispatchers.IO){
            repository.getProductDetails(id)
                .catch {
                    e -> _productDetails.emit(ApiState.Error(e))
                }
                .collect{
                    _productDetails.emit(ApiState.Success(it))
            }
        }
    }
}