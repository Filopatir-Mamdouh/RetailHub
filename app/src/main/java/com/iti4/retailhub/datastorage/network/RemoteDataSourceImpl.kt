package com.iti4.retailhub.datastorage.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
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


    override  fun getMyBagProducts(query: String): Flow<GetDraftOrdersByCustomerQuery.DraftOrders> = flow{
        val response = apolloClient.query(GetDraftOrdersByCustomerQuery(query)).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.draftOrders)
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

}