package com.iti4.retailhub.datastorage.network

sealed class ApiState {
    data class Success<out T>(val data: T) : ApiState()
    data class Error(val exception: String) : ApiState()
    object Loading : ApiState()
}