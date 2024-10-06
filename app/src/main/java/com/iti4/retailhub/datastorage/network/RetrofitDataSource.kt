package com.iti4.retailhub.datastorage.network


import com.iti4.retailhub.features.summary.Customer
import com.iti4.retailhub.features.summary.PaymentRequest
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface RetrofitDataSource {

    suspend fun  createStripeUser(
        name: String, email: String
    ): Response<Customer>

      fun createStripePaymentIntent(
          paymentRequest: PaymentRequest
    ): Flow<Response<ResponseBody>>
}