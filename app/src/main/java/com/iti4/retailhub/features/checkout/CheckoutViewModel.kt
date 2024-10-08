package com.iti4.retailhub.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.summary.PaymentIntentResponse
import com.iti4.retailhub.features.summary.PaymentRequest
import com.iti4.retailhub.models.AddressInputModel
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.CustomerInputModel
import com.iti4.retailhub.models.DraftOrderInputModel
import com.iti4.retailhub.models.toLineItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val dispatcher = Dispatchers.IO
    private lateinit var customerEmail: String
    private lateinit var customerName: String

    private val _customerDataResponse = MutableStateFlow<ApiState>(ApiState.Loading)
    val customerResponse = _customerDataResponse.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    private val _checkoutDraftOrderCreated = MutableStateFlow<ApiState>(ApiState.Loading)
    val checkoutDraftOrderCreated = _checkoutDraftOrderCreated.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    private val _paymentIntentResponse = MutableStateFlow<ApiState>(ApiState.Loading)
    val paymentIntentResponse = _paymentIntentResponse.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)


    val customerId by lazy {(repository.getUserShopLocalId()!!)}
    fun getCustomerData() {
        viewModelScope.launch(dispatcher) {
            repository.getCustomerInfoById(customerId)
                .catch { e ->
                    _customerDataResponse.emit(ApiState.Error(e))
                }
                .collect {
                    if (it.email.isNullOrEmpty())
                        _customerDataResponse.emit(ApiState.Error(throw Exception("Customer not found")))
                    else {
                        customerName = it.firstName + it.lastName
                        customerEmail = it.email!!
                        _customerDataResponse.emit(ApiState.Success(it))
                    }

                }
        }
    }

    fun createCheckoutDraftOrder(listOfCartProduct: List<CartProduct>, isCard: Boolean) {
        //    val customerData = CustomerInput("customer_id:6945540800554")
        viewModelScope.launch(dispatcher) {
            val lineItems = listOfCartProduct.map { it.toLineItem() }
            val customerId = "gid://shopify/Customer/6945540800554"
            val customerInputModel =
                CustomerInputModel(customerId, customerName, customerName, customerEmail)
            val addressInputModel =
                AddressInputModel("3 NewBridge Court", "Chino Hills", "CA", "91709", "USA")
            val draftOrderInputModel = DraftOrderInputModel(
                lineItems, customerInputModel, addressInputModel, customerEmail, null, false
            )
            listOfCartProduct.forEach {
                repository.deleteMyBagItem(it.draftOrderId).collect {}
            }
            repository.createCheckoutDraftOrder(draftOrderInputModel).catch { e ->
                _checkoutDraftOrderCreated.emit(ApiState.Error(e))
            }.collect { draftId ->
                repository.emailCheckoutDraftOrder(draftId.draftOrder!!.id).collect {
                    repository.completeCheckoutDraftOrder(draftId.draftOrder.id)
                        .collect { responseFromComplete ->
                            if(isCard){repository.markOrderAsPaid(responseFromComplete.order!!.id).collect{}}
                            repository.deleteMyBagItem(responseFromComplete.id).collect {
                                _checkoutDraftOrderCreated.emit(ApiState.Success(it))
                            }
                        }
                }
            }

        }
    }

    fun createPaymentIntent(orderPrice: Int) {
        viewModelScope.launch(dispatcher) {
            repository.createStripePaymentIntent(
                PaymentRequest(
                    customerEmail, customerName, orderPrice, "Egp"
                )
            ).collect {
                val response = it
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        if (jsonObject.has("clientSecret")) {
                            _paymentIntentResponse.value = ApiState.Success(
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