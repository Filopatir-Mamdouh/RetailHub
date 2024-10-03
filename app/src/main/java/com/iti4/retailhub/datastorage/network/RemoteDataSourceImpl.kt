package com.iti4.retailhub.datastorage.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.ProductsQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor (private val apolloClient: ApolloClient): RemoteDataSource {
    override  fun getProducts(query: String): Flow<ProductsQuery.Products> = flow{
        val response = apolloClient.query(ProductsQuery(query)).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.products)
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

    override  fun getBrands(): Flow<CollectionsQuery.Collections> = flow{
        val response = apolloClient.query(CollectionsQuery()).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.collections)
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

    override fun getOrders(query: String): Flow<OrdersQuery.Orders> = flow{
        val response = apolloClient.query(OrdersQuery(query)).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.orders)
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

}