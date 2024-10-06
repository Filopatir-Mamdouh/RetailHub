package com.iti4.retailhub.datastorage

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.ProductsQuery

import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.features.payments.Customer
import com.iti4.retailhub.features.payments.PaymentRequest
import com.iti4.retailhub.models.CartProduct

import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.Category
import com.iti4.retailhub.models.HomeProducts

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface IRepository {

    fun getMyBagProducts(query: String): Flow<List<CartProduct>>
    fun deleteMyBagItem(query: String): Flow<DeleteDraftOrderMutation.DraftOrderDelete>
    fun updateMyBagItem(cartProduct: CartProduct): Flow<UpdateDraftOrderMutation.DraftOrderUpdate>
    fun createStripeUser(): Flow<Response<Customer>>
    fun createStripePaymentIntent(paymentRequest: PaymentRequest): Flow<Response<ResponseBody>>

    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>

    fun getProductTypesOfCollection(): Flow<List<Category>>
}