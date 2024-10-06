package com.iti4.retailhub.datastorage.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.CreateDraftOrderMutation
import com.iti4.retailhub.CustomerEmailSearchQuery
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.logic.toBrandsList
import com.iti4.retailhub.logic.toProductsList
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import com.iti4.retailhub.type.DraftOrderInput
import com.iti4.retailhub.type.DraftOrderLineItemInput
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

    override  fun getProductDetails(id: String): Flow<ProductDetailsQuery.OnProduct?> = flow{
        val response = apolloClient.query(ProductDetailsQuery(id)).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data?.node?.onProduct)
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

    override fun insertMyBagItem(varientId: String, customerId: String): Flow<CreateDraftOrderMutation.DraftOrderCreate> =
        flow {
            Log.d("TAG", "insertMyBagItem:start ")
            val draftOrderInput = createDraftOrderFromVairentOnly(varientId,customerId)
            val response =
                apolloClient.mutation(CreateDraftOrderMutation(draftOrderInput)).execute()
            Log.d("TAG", "insertMyBagItem:response ${response} ")
            if (!response.hasErrors() && response.data != null) {
                Log.d("TAG", "insertMyBagItem:if ${response.data!!.draftOrderCreate!!}")
                emit(response.data!!.draftOrderCreate!!)
            } else {
                Log.d("TAG", "insertMyBagItem:else ${response.errors?.get(0)?.message ?: "Something went wrong"}")
                throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
            }
        }
    fun createDraftOrderFromVairentOnly(varientId: String, customerId: String): DraftOrderInput {
        return DraftOrderInput(
            lineItems = Optional.present(
                listOf(
                    DraftOrderLineItemInput(
                        variantId = Optional.present(varientId), quantity = 1
                    )
                )
            ),
            customerId = Optional.present(customerId)
        )
    }
    override fun GetDraftOrdersByCustomer(varientId: String): Flow<GetDraftOrdersByCustomerQuery.DraftOrders> =
        flow {
            val response =
                apolloClient.query(GetDraftOrdersByCustomerQuery(varientId)).execute()
            if (!response.hasErrors() && response.data != null) {
                emit(response.data!!.draftOrders)
            } else {
                throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
            }
        }
}