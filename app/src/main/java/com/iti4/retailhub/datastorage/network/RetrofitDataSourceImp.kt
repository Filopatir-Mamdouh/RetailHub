package com.iti4.retailhub.datastorage.network


import com.iti4.retailhub.BuildConfig
import com.iti4.retailhub.features.summary.Customer
import com.iti4.retailhub.features.summary.PaymentRequest
import com.iti4.retailhub.modelsdata.PlaceLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class RetrofitDataSourceImp @Inject constructor(
    private val apiService: ApiService,
    private val apiServiceForLocation: ApiServiceForLocation
) :
    RetrofitDataSource {

    private val Location_API_KEY = BuildConfig.Location_API_KEY

    override suspend fun createStripeUser(
        name: String, email: String
    ): Response<Customer> {
        val user = HashMap<String, String>();
        user.put("email", "user@example.com");
        user.put("name", "User Name");
        return apiService.createStripeUser(user)
    }

    override fun createStripePaymentIntent(
        paymentRequest: PaymentRequest
    ): Flow<Response<ResponseBody>> = flow {
        emit(apiService.getStripePaymentIntent(paymentRequest))
    }

    override fun getLocationSuggestions(
        query: String
    ): Flow<Response<List<PlaceLocation>>> = flow {
        emit(apiServiceForLocation.getLocationSuggestions(Location_API_KEY, query, 5, 1))
    }


}