package com.iti4.retailhub.features.productdetails.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
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
    val customerId by lazy {repository.getUserShopLocalId()}

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
fun  GetDraftOrdersByCustomer(varientId: String){
    viewModelScope.launch(Dispatchers.IO){
        repository.GetDraftOrdersByCustomer(varientId)
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
        variantID: String,
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
                            value = Optional.present(variantID),
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
}


