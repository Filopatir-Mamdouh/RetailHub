package com.iti4.retailhub.features.mybag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
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
class MyBagViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    val dispatcher = Dispatchers.IO
    private val _myBagProducts = MutableStateFlow<ApiState>(ApiState.Loading)
    val products = _myBagProducts.onStart { getMyBagProducts() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)


    var totalPrice = 0.0

    private fun getMyBagProducts() {
        viewModelScope.launch(dispatcher) {
            repository.getMyBagProducts("customer_id:6945540964394")
                .catch { e -> _myBagProducts.emit(ApiState.Error(e)) }.collect {
                    _myBagProducts.emit(ApiState.Success(it))
                }
        }
    }

    fun updateTotalPrice(nodes: List<GetDraftOrdersByCustomerQuery.Node1>) {
         totalPrice = nodes.sumOf {
            val price = it.variant?.price?.toString()?.toDoubleOrNull() ?: 0.0
            price * it.quantity.toDouble()
        }

    }
}