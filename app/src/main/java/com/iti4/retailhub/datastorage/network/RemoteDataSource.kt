package com.iti4.retailhub.datastorage.network

import com.apollographql.apollo.api.ApolloResponse
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.ProductsQuery


interface RemoteDataSource {
    suspend fun getProducts(query: String): ApolloResponse<ProductsQuery.Data>
    suspend fun getBrands(): ApolloResponse<CollectionsQuery.Data>
}