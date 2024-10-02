package com.iti4.retailhub.datastorage

import com.iti4.retailhub.datastorage.network.ApiState
import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun getProducts(query: String): Flow<ApiState>
    fun getBrands(): Flow<ApiState>
}