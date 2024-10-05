package com.iti4.retailhub.datastorage.network

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.CustomerEmailSearchQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import kotlinx.coroutines.flow.Flow


interface RemoteDataSource {
    fun createUser(input: CustomerInput): Flow<CreateCustomerMutation.CustomerCreate>
    fun getCustomerIdByEmail(email: String): Flow<CustomerEmailSearchQuery.Customers>
    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>
    fun getOrders(query: String): Flow<OrdersQuery.Orders>
}