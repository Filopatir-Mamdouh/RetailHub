package com.iti4.retailhub.datastorage

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import com.iti4.retailhub.datastorage.network.RetrofitDataSource
import com.iti4.retailhub.features.payments.Customer
import com.iti4.retailhub.features.payments.PaymentRequest
import com.iti4.retailhub.models.CartProduct

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val retrofitDataSource: RetrofitDataSource
) : IRepository {
    override fun getProducts(query: String): Flow<ProductsQuery.Products> {
        return remoteDataSource.getProducts(query)
    }

    override fun getBrands(): Flow<CollectionsQuery.Collections> {
        return remoteDataSource.getBrands()
    }

    override fun getMyBagProducts(query: String): Flow<List<CartProduct>> {
        return remoteDataSource.getMyBagProducts(query)
    }

    override fun deleteMyBagItem(query: String): Flow<DeleteDraftOrderMutation.DraftOrderDelete> {
        return remoteDataSource.deleteMyBagItem(query)
    }

    override fun updateMyBagItem(cartProduct: CartProduct): Flow<UpdateDraftOrderMutation.DraftOrderUpdate> {
        return remoteDataSource.updateMyBagItem(cartProduct)
    }

    override fun createStripeUser(): Flow<Response<Customer>> = flow {
        retrofitDataSource.createStripeUser("ahmed", "ahmed@gmail.com")
    }

    override fun createStripePaymentIntent(paymentRequest: PaymentRequest): Flow<Response<ResponseBody>> {
        return retrofitDataSource.createStripePaymentIntent(paymentRequest)
    }


}