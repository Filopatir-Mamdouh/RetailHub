package com.iti4.retailhub.features.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.payments.PaymentIntentResponse
import com.iti4.retailhub.features.payments.PaymentRequest
import com.iti4.retailhub.models.AddressInputModel
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.CustomerInputModel
import com.iti4.retailhub.models.DraftOrderInputModel
import com.iti4.retailhub.models.toLineItem
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


    fun createCheckoutDraftOrder(listOfCartProduct: List<CartProduct>) {
        //    val customerData = CustomerInput("customer_id:6945540800554")

        viewModelScope.launch(dispatcher) {
            val lineItems = listOfCartProduct.map { it.toLineItem() }
            val customerId = "gid://shopify/Customer/6945540800554"
            repository.getCustomerInfoById(customerId).collect {
                val customerInputModel = CustomerInputModel(customerId, it.firstName, it.lastName, it.email)
                val addressInputModel =
                    AddressInputModel("3 NewBridge Court", "Chino Hills", "CA", "91709", "USA")
                val draftOrderInputModel =
                    DraftOrderInputModel(lineItems, customerInputModel, addressInputModel, it.email, null, false)
                listOfCartProduct.forEach{
                    Log.i("here", "deleting" + it.draftOrderId)
                    repository.deleteMyBagItem(it.draftOrderId).collect{}}
                repository.createCheckoutDraftOrder(draftOrderInputModel).collect{
                    Log.i("here", "createCheckoutDraftOrder: "+it.toString())
                }
            }

        }
    }

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