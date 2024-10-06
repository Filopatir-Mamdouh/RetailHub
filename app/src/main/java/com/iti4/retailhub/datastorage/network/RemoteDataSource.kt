package com.iti4.retailhub.datastorage.network

import com.iti4.retailhub.CompleteDraftOrderMutation
import com.iti4.retailhub.CreateDraftOrderMutation
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.DraftOrderInvoiceSendMutation
import com.iti4.retailhub.GetCustomerByIdQuery
import com.iti4.retailhub.MarkAsPaidMutation
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.DraftOrderInputModel
import com.iti4.retailhub.models.HomeProducts
import kotlinx.coroutines.flow.Flow


interface RemoteDataSource {

    fun getMyBagProducts(query: String): Flow<List<CartProduct>>
    fun deleteMyBagItem(query: String): Flow<DeleteDraftOrderMutation.DraftOrderDelete>
    fun updateMyBagItem(cartProduct: CartProduct): Flow<UpdateDraftOrderMutation.DraftOrderUpdate>
    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>
    fun getOrders(query: String): Flow<OrdersQuery.Orders>
    fun getCustomerInfoById(id: String): Flow<GetCustomerByIdQuery.Customer>
    fun createCheckoutDraftOrder(draftOrderInputModel: DraftOrderInputModel): Flow<CreateDraftOrderMutation.DraftOrderCreate>
    fun emailCheckoutDraftOrder(draftOrderId: String): Flow<DraftOrderInvoiceSendMutation.DraftOrder>
    fun completeCheckoutDraftOrder(draftOrderId: String): Flow<CompleteDraftOrderMutation.DraftOrder>
    fun insertMyBagItem(varientId: String, customerId: String): Flow<CreateDraftOrderMutation.DraftOrderCreate>
    fun markOrderAsPaid(orderId: String): Flow<MarkAsPaidMutation.OrderMarkAsPaid>
}