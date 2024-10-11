package com.iti4.retailhub.features.productdetails.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CountryCodes
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.type.MetafieldInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(private val repository: IRepository): ViewModel() {
    private val _productDetails = MutableStateFlow<ApiState>(ApiState.Loading)
    val productDetails = _productDetails
    private val _createDraftOrder = MutableStateFlow<ApiState>(ApiState.Loading)
    val createDraftOrder = _createDraftOrder
    private val _customerDraftOrders = MutableStateFlow<ApiState>(ApiState.Loading)
    val customerDraftOrders = _customerDraftOrders
    private val _saveProductToFavortes = MutableStateFlow<ApiState>(ApiState.Loading)
    val saveProductToFavortes = _saveProductToFavortes
    private val _productInFavorites = MutableStateFlow<ApiState>(ApiState.Loading)
    val productInFavorites = _productInFavorites
//    val customerId by lazy {repository.getUserShopLocalId()}
    val customerId ="gid://shopify/Customer/6945540800554"

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
fun  GetDraftOrdersByCustomer(productTitle:String){
    val regex = """\/([^\/]+)$""".toRegex()
    val matchResult = regex.find(customerId)
    val customerIdNumberOnly = matchResult?.groupValues?.get(1)
    viewModelScope.launch(Dispatchers.IO){
        repository.GetDraftOrdersByCustomer("(customer_id:${customerIdNumberOnly}) ${productTitle}")
            .catch {
                    e -> _customerDraftOrders.emit(ApiState.Error(e))
            }
            .collect{
                _customerDraftOrders.emit(ApiState.Success(it))
    }
}
}
    fun addToCart(selectedProductVariantId: String) {

        viewModelScope.launch(Dispatchers.IO){
            Log.d("TAG", "addToCart:launch ")
            /*if (customerId != null) {*/
                Log.d("TAG", "addToCart:if ")
                repository.insertMyBagItem(selectedProductVariantId,customerId!!)
                    .catch { e ->
                        _createDraftOrder.emit(ApiState.Error(e))
                        Log.d("TAG", "addToCart:catch ${e.message}")
                    }
                    .collect{
                        _createDraftOrder.emit(ApiState.Success(it))
                        Log.d("TAG", "addToCart:collect ${it} ")
                    }
//            }
        }
    }

    fun saveToFavorites(
        variantID: String,productId:String,
        selectedProductColor: String,
        selectedProductSize: String,
        productTitle: String,
        selectedImage: String,
        price: String
    ) {
        viewModelScope.launch(Dispatchers.IO){

            repository.saveProductToFavotes(CustomerInput(
                id = Optional.present(customerId),
                metafields = Optional.present(
                    listOf(
                        MetafieldInput(
                            namespace= Optional.present(variantID),
                            key = Optional.present("favorites"),
                            value = Optional.present(productId),
                            type = Optional.present("string"),
                            description = Optional.present("${productTitle},${selectedProductColor},${selectedProductSize},${selectedImage},${price}")
            )
                    )
                )))
                .catch { e ->
                    Log.d("fav", "saveToFavorites:${e.message} ")
                    _saveProductToFavortes.emit(ApiState.Error(e))
                }
                .collect{
                    Log.d("fav", "saveToFavorites:${it} ")
                    _saveProductToFavortes.emit(ApiState.Success(it))
                }
        }
    }

    fun searchProductInCustomerFavorites(selectedProductVariantId: String) {
        val regex = """\/([^\/]+)$""".toRegex()
        val matchResult = regex.find(selectedProductVariantId)
        val id = selectedProductVariantId.split("/").last()
        viewModelScope.launch(Dispatchers.IO){
            Log.d("TAG", "addToCart:launch ")
            /*if (customerId != null) {*/
            Log.d("searchInCustomerFavorites", "gid://shopify/ProductVariant/${id}\"")
            repository.getCustomerFavoritesoById(customerId,selectedProductVariantId.toString())
                .catch { e ->
                    _productInFavorites.emit(ApiState.Error(e))
                    Log.d("TAG", "addToCart:catch ${e.message}")
                }
                .collect{
                    _productInFavorites.emit(ApiState.Success(it))
                    Log.d("TAG", "addToCart:collect ${it} ")
                }
//            }
        }
    }

    fun getConversionRates(currencyCode: CountryCodes): Double {
        return repository.getConversionRates(currencyCode)
    }

    fun getCurrencyCode(): CountryCodes {
        return repository.getCurrencyCode()
    }
}


