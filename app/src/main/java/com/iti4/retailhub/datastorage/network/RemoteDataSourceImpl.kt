package com.iti4.retailhub.datastorage.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.ProductsQuery
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor (private val apolloClient: ApolloClient): RemoteDataSource {
    override suspend fun getProducts(query: String): ApolloResponse<ProductsQuery.Data> {
        return apolloClient.query(ProductsQuery(query)).execute()
    }

    override suspend fun getBrands(): ApolloResponse<CollectionsQuery.Data> {
        return apolloClient.query(CollectionsQuery()).execute()
    }

}