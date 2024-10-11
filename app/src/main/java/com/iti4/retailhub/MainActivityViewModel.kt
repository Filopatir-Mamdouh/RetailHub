package com.iti4.retailhub

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CountryCodes
import com.iti4.retailhub.models.CustomerAddressV2
import com.iti4.retailhub.models.Discount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val TAG: String = "MainActivityViewModel"

    val customerId by lazy { (repository.getUserShopLocalId()!!) }
    private val dispatcher = Dispatchers.IO

    // used for addresses
    var indexOfLastDefaultAddress = 99
    var customerChoseAnAddressNotDefault = false
    lateinit var customerChosenAddress: CustomerAddressV2

    // for getting currency everyday and firstitme
    private val _currencyState = MutableStateFlow<ApiState>(ApiState.Loading)
    val currencyState = _currencyState.asStateFlow()

    //
    var copiedCouponsList: MutableList<Discount> = mutableListOf()
    var discountList: MutableList<Discount>? = null
    var usedDiscountCodesList: List<String> = listOf()

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

    fun getConversionRates(currencyCode: CountryCodes): Double {
        return repository.getConversionRates(currencyCode)
    }

    fun getCurrencyCode(): CountryCodes {
        return repository.getCurrencyCode()
    }

    fun getDiscount() {
        viewModelScope.launch(dispatcher) {
            repository.getDiscounts().catch { e ->
                Log.i("here", "getDiscount: No Discounts ")
            }.collect {
                discountList = it.toMutableList()
            }
        }
    }

    fun getUsedDiscounts() {
        viewModelScope.launch(dispatcher) {
            repository.getCustomerUsedDiscounts(customerId).catch { e ->
                Log.i("here", "getDiscount: No Discounts ")
            }.collect {
                usedDiscountCodesList = it
            }
        }
    }

}