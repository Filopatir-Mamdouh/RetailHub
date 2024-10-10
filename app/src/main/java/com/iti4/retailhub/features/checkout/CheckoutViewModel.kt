package com.iti4.retailhub.features.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.summary.PaymentIntentResponse
import com.iti4.retailhub.features.summary.PaymentRequest
import com.iti4.retailhub.models.AddressInputModel
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.CustomerInputModel
import com.iti4.retailhub.models.Discount
import com.iti4.retailhub.models.DiscountInput
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
    var selectedDiscount: Discount? = null

    private val _customerDataResponse = MutableStateFlow<ApiState>(ApiState.Loading)
    val customerResponse = _customerDataResponse.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    private val _checkoutDraftOrderCreated = MutableStateFlow<ApiState>(ApiState.Loading)
    val checkoutDraftOrderCreated = _checkoutDraftOrderCreated.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    private val _paymentIntentResponse = MutableStateFlow<ApiState>(ApiState.Loading)
    val paymentIntentResponse = _paymentIntentResponse.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    private val _addressesState = MutableStateFlow<ApiState>(ApiState.Loading)
    val addressesState = _addressesState.onStart {}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)

    val customerId by lazy { (repository.getUserShopLocalId()!!) }


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

    fun getDefaultAddress() {
        viewModelScope.launch(dispatcher) {
            repository.getDefaultAddress(customerId!!)
                .catch { e -> _addressesState.emit(ApiState.Error(e)) }.collect {
                    _addressesState.emit(ApiState.Success(it))
                }
        }
    }


    fun createCheckoutDraftOrder(listOfCartProduct: List<CartProduct>, isCard: Boolean) {
        //    val customerData = CustomerInput("customer_id:6945540800554")
        Log.i("here", "simple call: ")
        viewModelScope.launch(dispatcher) {
            Log.i("here", "createCheckoutDraftOrder: ")
            val lineItems = listOfCartProduct.map { it.toLineItem() }
            val customerInputModel =
                CustomerInputModel(customerId, customerName, customerName, customerEmail)
            var discount: DiscountInput? = null
            if (selectedDiscount != null) {
                discount = DiscountInput(
                    selectedDiscount!!.getDiscountAsDouble(), selectedDiscount!!.title
                )
            }
            val addressInputModel =
                AddressInputModel("3 NewBridge Court", "Chino,Hills,sfdASD", "CA", "91709", "USA")
            val draftOrderInputModel = DraftOrderInputModel(
                lineItems, customerInputModel, addressInputModel, customerEmail, discount, false
            )
            listOfCartProduct.forEach {
                repository.deleteMyBagItem(it.draftOrderId).catch {
                    Log.i("here", "deleteMyBagItem: " + it.message)
                }.collect {
                    Log.i("here", "deleteMyBagItem: " + it.deletedId)
                }
            }
            repository.createCheckoutDraftOrder(draftOrderInputModel)
                .catch { e ->
                    Log.i("here", "he big one:: " + e)
                    _checkoutDraftOrderCreated.emit(ApiState.Error(e))
                }.collect { draftId ->
                    Log.i("here", "the big one: " + draftId)
                    repository.emailCheckoutDraftOrder(draftId.draftOrder!!.id).collect {
                        Log.i("here", "emailCheckoutDraftOrder: " + it.id)
                        repository.completeCheckoutDraftOrder(draftId.draftOrder.id)
                            .collect { responseFromComplete ->
                                Log.i("here", "completeCheckoutDraftOrder: ")
                                if (discount != null) {
                                    repository.setCustomerUsedDiscounts(
                                        customerId,
                                        discount.valueType
                                    )
                                        .collect {
                                            Log.i("here", "setCustomerUsedDiscounts: ")
                                        }
                                }
                                if (isCard) {
                                    repository.markOrderAsPaid(responseFromComplete.order!!.id)
                                        .collect {
                                            Log.i("here", "markOrderAsPaid: ")
                                        }
                                }
                                repository.deleteMyBagItem(responseFromComplete.id).collect {
                                    Log.i("here", "deleteMyBagItem: ")
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
                    customerEmail, customerName, orderPrice, repository.getCurrencyCode().toString()
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