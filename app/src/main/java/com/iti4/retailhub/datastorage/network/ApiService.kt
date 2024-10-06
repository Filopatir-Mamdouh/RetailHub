package com.iti4.retailhub.datastorage.network



import com.iti4.retailhub.features.summary.Customer
import com.iti4.retailhub.features.summary.PaymentRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("createuser")
    suspend fun createStripeUser(
        @Body user: Map<String, String>
    ): Response<Customer>

    @POST("paymentintent")
    suspend fun getStripePaymentIntent(
        @Body paymentRequest: PaymentRequest
    ): Response<ResponseBody>
}


object RetrofitHelper {
    private const val BASE_URL = "https://nodejs-serverless-function-express-self-eta.vercel.app/api/"
    val retrofitInstance = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
}
