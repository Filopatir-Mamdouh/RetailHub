package com.iti4.retailhub.datastorage

import com.iti4.retailhub.CompleteDraftOrderMutation
import com.iti4.retailhub.CreateDraftOrderMutation
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.DraftOrderInvoiceSendMutation
import com.iti4.retailhub.GetCustomerByIdQuery

import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.features.payments.PaymentRequest
import com.iti4.retailhub.models.CartProduct

import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.DraftOrderInputModel
import com.iti4.retailhub.models.HomeProducts

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface IRepository {

    fun getMyBagProducts(query: String): Flow<List<CartProduct>>
    fun deleteMyBagItem(query: String): Flow<DeleteDraftOrderMutation.DraftOrderDelete>
    fun updateMyBagItem(cartProduct: CartProduct): Flow<UpdateDraftOrderMutation.DraftOrderUpdate>
    fun createStripePaymentIntent(paymentRequest: PaymentRequest): Flow<Response<ResponseBody>>
    fun getCustomerInfoById(id: String): Flow<GetCustomerByIdQuery.Customer>
    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>
    fun  createCheckoutDraftOrder(draftOrderInputModel: DraftOrderInputModel): Flow<CreateDraftOrderMutation.DraftOrderCreate>
    fun emailCheckoutDraftOrder(draftOrderId:String ): Flow<DraftOrderInvoiceSendMutation.DraftOrder>
    fun completeCheckoutDraftOrder(draftOrderId:String ): Flow<CompleteDraftOrderMutation.DraftOrder>
}