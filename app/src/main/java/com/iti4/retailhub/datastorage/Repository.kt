package com.iti4.retailhub.datastorage

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(private val remoteDataSource: RemoteDataSource) : IRepository {
    override fun getProducts(query: String) : Flow<List<HomeProducts>> {
        return remoteDataSource.getProducts(query)
    }
    override fun getBrands() : Flow<List<Brands>> {
        return remoteDataSource.getBrands()
    }
}