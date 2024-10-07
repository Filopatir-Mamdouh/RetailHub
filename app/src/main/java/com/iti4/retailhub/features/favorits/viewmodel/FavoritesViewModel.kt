package com.iti4.retailhub.features.favorits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: IRepository): ViewModel() {
    private val _savedFavortes = MutableStateFlow<ApiState>(ApiState.Loading)
    val savedFavortes = _savedFavortes
    val customerId by lazy {repository.getUserShopLocalId()}

    fun  getFavorites(){
        viewModelScope.launch(Dispatchers.IO){
            repository.getCustomerFavoritesoById(customerId!!)
                .catch {
                        e -> _savedFavortes.emit(ApiState.Error(e))
                }
                .collect{
                    _savedFavortes.emit(ApiState.Success(it))
                }
        }
    }
}