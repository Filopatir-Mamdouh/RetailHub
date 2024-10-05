package com.iti4.retailhub.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.payments.PaymentIntentResponse
import com.iti4.retailhub.features.payments.PaymentRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val dispatcher = Dispatchers.IO
//    private val _myBagProducts = MutableStateFlow<ApiState>(ApiState.Loading)
//    val products = _myBagProducts.onStart { getMyBagProducts() }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    private val _paymentIntentResponse = MutableStateFlow<ApiState>(ApiState.Loading)
    val paymentIntentResponse = _paymentIntentResponse.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)


    fun createPaymentIntent() {
        viewModelScope.launch(dispatcher) {
            repository.createStripePaymentIntent(
                PaymentRequest(
                    "ahmed123@gmail.com",
                    "ahmed",
                    400,
                    "usd"
                )
            ).collect {
                val response = it
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        if (jsonObject.has("clientSecret")) {
                            _paymentIntentResponse.value =
                                ApiState.Success(
                                    PaymentIntentResponse(
                                        jsonObject.getString("clientSecret"),
                                        jsonObject.getString("dpmCheckerLink"),
                                        jsonObject.getString("ephemeralKey"),
                                        jsonObject.getString("customer")
                                    )
                                )
                        } else {
                            ApiState.Error(throw Exception(jsonObject.getString("error")))
                        }
                    }
                } else {
                    ApiState.Error(throw Exception())
                }
            }
        }
    }


}