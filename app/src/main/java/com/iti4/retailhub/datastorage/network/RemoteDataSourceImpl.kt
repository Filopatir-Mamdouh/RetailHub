package com.iti4.retailhub.datastorage.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.CustomerEmailSearchQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.logic.toBrandsList
import com.iti4.retailhub.logic.toProductsList
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor (private val apolloClient: ApolloClient): RemoteDataSource {
    override  fun getProducts(query: String): Flow<List<HomeProducts>> = flow{
        val response = apolloClient.query(ProductsQuery(query)).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.products.toProductsList())
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }
    override  fun getCustomerIdByEmail(email: String): Flow<CustomerEmailSearchQuery.Customers> = flow{
        val response = apolloClient.query(CustomerEmailSearchQuery(email)).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.customers)
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

    override fun createUser(input: CustomerInput) : Flow<CreateCustomerMutation.CustomerCreate> = flow {
    val response = apolloClient.mutation(CreateCustomerMutation(input)).execute()
    if (!response.hasErrors() && response.data != null){
            emit(response.data!!.customerCreate!!)
    }
    else {
        throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
    }

}
    override  fun getBrands(): Flow<List<Brands>> = flow{
        val response = apolloClient.query(CollectionsQuery()).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.collections.toBrandsList())
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

    override fun getOrders(query: String): Flow<OrdersQuery.Orders> = flow{
        val response = apolloClient.query(OrdersQuery(query)).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!.orders)
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

}