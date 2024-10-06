package com.iti4.retailhub.datastorage.network

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.CreateDraftOrderMutation
import com.iti4.retailhub.CustomerEmailSearchQuery
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import kotlinx.coroutines.flow.Flow


interface RemoteDataSource {
    fun createUser(input: CustomerInput): Flow<CreateCustomerMutation.CustomerCreate>
    fun getCustomerIdByEmail(email: String): Flow<CustomerEmailSearchQuery.Customers>
    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>
    fun getOrders(query: String): Flow<OrdersQuery.Orders>
    fun getProductDetails(id: String): Flow<ProductDetailsQuery.OnProduct?>
    fun insertMyBagItem(
        varientId: String,
        customerId: String
    ): Flow<CreateDraftOrderMutation.DraftOrderCreate>

    fun GetDraftOrdersByCustomer(varientId: String): Flow<GetDraftOrdersByCustomerQuery.DraftOrders>
}