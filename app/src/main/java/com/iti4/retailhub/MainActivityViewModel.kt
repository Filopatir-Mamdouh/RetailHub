package com.iti4.retailhub

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CountryCodes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val _currencyState = MutableStateFlow<ApiState>(ApiState.Loading)
    val currencyState = _currencyState.asStateFlow()
    private val dispatcher = Dispatchers.IO

    fun getCurrencyRates() {
        viewModelScope.launch(dispatcher) {
            repository.getCurrencyRates()
                .catch { e -> _currencyState.emit(ApiState.Error(e)) }.collect {
                    _currencyState.emit(ApiState.Success(it))
                }
        }
    }

    fun getFirstTime(): Boolean {
        return repository.getFirstTime()
    }

    fun setFirstTime() {
        repository.setFirstTime()
    }

    fun getShouldIRefrechCurrency(): Boolean {
        return repository.getShouldIRefrechCurrency()
    }

    fun setRefrechCurrency() {
        repository.setRefrechCurrency()
    }

    fun saveConversionRates(conversion_rates: Map<String, Double>) {
        repository.saveConversionRates(conversion_rates)
    }

    fun getConversionRates(currencyCode: CountryCodes) {
        Log.i("here", "getConversionRates: " + repository.getConversionRates(currencyCode))

    }


}