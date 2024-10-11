package com.iti4.retailhub.features.productSearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductSEarchViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val dispatcher = Dispatchers.IO
    private val _searchList = MutableStateFlow<ApiState>(ApiState.Loading)
    val searchList = _searchList.stateIn(viewModelScope, SharingStarted.Lazily, ApiState.Loading)
    fun searchProducts(query: String) {
        viewModelScope.launch(dispatcher){
            Log.d("search", "viewModelScope: start")
            repository.getProducts(query).catch {
                    e ->Log.d("search", "viewModelScope error: ${e.message}")
                _searchList.emit(ApiState.Error(e))
            }.collect{
                Log.d("search", "viewModelScope collect: ${it}")
                _searchList.emit(ApiState.Success(it))
            }
        }
    }
}