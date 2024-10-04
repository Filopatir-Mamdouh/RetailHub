package com.iti4.retailhub.datastorage

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(private val remoteDataSource: RemoteDataSource) : IRepository {
    override fun getProducts(query: String) : Flow<ProductsQuery.Products> {
        return remoteDataSource.getProducts(query)
    }
    override fun getBrands() : Flow<CollectionsQuery.Collections> {
        return remoteDataSource.getBrands()
    }

    override fun getMyBagProducts(query: String): Flow<GetDraftOrdersByCustomerQuery.DraftOrders> {
        return remoteDataSource.getMyBagProducts(query)
    }


}