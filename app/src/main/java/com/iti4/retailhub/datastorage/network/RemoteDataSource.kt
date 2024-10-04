package com.iti4.retailhub.datastorage.network

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import kotlinx.coroutines.flow.Flow


interface RemoteDataSource {
    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>
    fun getOrders(query: String): Flow<OrdersQuery.Orders>
}