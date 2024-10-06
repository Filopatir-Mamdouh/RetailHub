package com.iti4.retailhub.datastorage.network

sealed class ApiState {
    data class Success<out T>(val data: T) : ApiState()
    data class Error(val exception: Throwable) : ApiState()
    object Loading : ApiState()
}