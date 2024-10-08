package com.iti4.retailhub.datastorage.network


import com.iti4.retailhub.features.summary.Customer
import com.iti4.retailhub.features.summary.PaymentRequest

import com.iti4.retailhub.modelsdata.PlaceLocation
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

interface ApiServiceForLocation {
    @GET("autocomplete")
    suspend fun getLocationSuggestions(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("dedupe") dedupe: Int
    ): Response<List<PlaceLocation>>

}

interface ApiServiceForLocationGeocoding {

    //    https://us1.locationiq.com/v1/reverse?key=Your_API_Access_Token&lat=31.043541&lon=29.788351&format=json&
    @GET("reverse")
    suspend fun getLocationGeocoding(
        @Query("key") apiKey: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("format") format: String
    ): Response<com.iti4.retailhub.features.address.PlaceLocation>
}


object RetrofitHelper {
    private const val BASE_URL =
        "https://nodejs-serverless-function-express-self-eta.vercel.app/api/"
    private const val BASE_URL_LOCATION = "https://api.locationiq.com/v1/"
    private const val BASE_URL_REVERSE_LOCATION = "https://eu1.locationiq.com/v1/"
    val retrofitInstance = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val retrofitInstanceForLocation = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_LOCATION)
        .build()

    val retrofitInstanceForReverseLocation = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_REVERSE_LOCATION)
        .build()

}
