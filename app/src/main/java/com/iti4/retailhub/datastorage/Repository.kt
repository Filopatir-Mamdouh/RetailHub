package com.iti4.retailhub.datastorage

import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(private val remoteDataSource: RemoteDataSource) : IRepository {

    override fun getProducts(query: String) : Flow<ApiState> = flow {
        val response = remoteDataSource.getProducts(query)
        if (response.hasErrors()){
            emit(ApiState.Error(response.errors?.get(0)?.message ?: ""))
        }
        else if ( response.data != null){
            emit(ApiState.Success(response.data))
        }
        else{
            emit(ApiState.Error("Something went wrong"))
        }
    }
    override fun getBrands() : Flow<ApiState> = flow {
        val response = remoteDataSource.getBrands()
        if (response.hasErrors()){
            emit(ApiState.Error(response.errors?.get(0)?.message ?: ""))
        }
        else if ( response.data != null){
            emit(ApiState.Success(response.data))
        }
        else{
            emit(ApiState.Error("Something went wrong"))
        }
    }
}