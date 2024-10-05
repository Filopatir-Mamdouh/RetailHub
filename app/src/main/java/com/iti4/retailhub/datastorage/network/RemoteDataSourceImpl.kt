package com.iti4.retailhub.datastorage.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.type.DraftOrderDeleteInput
import com.iti4.retailhub.type.DraftOrderInput
import com.iti4.retailhub.type.DraftOrderLineItemInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val apolloClient: ApolloClient) :
    RemoteDataSource {
    override fun getProducts(query: String): Flow<ProductsQuery.Products> = flow {
        val response = apolloClient.query(ProductsQuery(query)).execute()
        if (!response.hasErrors() && response.data != null) {
            emit(response.data!!.products)
        } else {
            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
        }
    }

    override fun getBrands(): Flow<CollectionsQuery.Collections> = flow {
        val response = apolloClient.query(CollectionsQuery()).execute()
        if (!response.hasErrors() && response.data != null) {
            emit(response.data!!.collections)
        } else {
            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
        }
    }


    override fun getMyBagProducts(query: String): Flow<List<CartProduct>> = flow {
        val response = apolloClient.query(GetDraftOrdersByCustomerQuery(query)).execute()
        if (!response.hasErrors() && response.data != null) {
            emit(extractCart(response.data!!.draftOrders))
        } else {
            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
        }
    }


    override fun deleteMyBagItem(query: String): Flow<DeleteDraftOrderMutation.DraftOrderDelete> =
        flow {
            val deleteDraftOrderInput = DraftOrderDeleteInput(id = query)
            val deleteDraftOrderMutation = DeleteDraftOrderMutation(deleteDraftOrderInput)
            val response = apolloClient.mutation(deleteDraftOrderMutation).execute()

            if (!response.hasErrors() && response.data != null) {
                emit(response.data!!.draftOrderDelete!!)
            } else {
                throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
            }
        }

    override fun updateMyBagItem(cartProduct: CartProduct): Flow<UpdateDraftOrderMutation.DraftOrderUpdate> =
        flow {
            val draftOrderInput = DraftOrderInput(
                lineItems = Optional.present(
                    listOf(
                        DraftOrderLineItemInput(
                            variantId = Optional.present(cartProduct.itemId),
                            quantity = cartProduct.itemQuantity
                        )
                    )
                )
            )
            val updateDraftOrderMutation =
                UpdateDraftOrderMutation(cartProduct.draftOrderId, draftOrderInput)
            val response = apolloClient.mutation(updateDraftOrderMutation).execute()
            if (!response.hasErrors() && response.data != null) {
                emit(response.data!!.draftOrderUpdate!!)
            } else {
                throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
            }
        }


    private fun extractCart(item: GetDraftOrdersByCustomerQuery.DraftOrders): List<CartProduct> {

        return item.nodes.map {
            val product = it.lineItems.nodes.get(0)
            val variant = product.variant
            val variantSize = variant!!.selectedOptions[0]
            val variantColor = variant!!.selectedOptions[1]
            val variantImage = variant.product.media.nodes[0].onMediaImage!!.image!!.url
            CartProduct(
                it!!.id,
                variant!!.id,
                product.quantity,
                variant.inventoryQuantity!!,
                product.title,
                variantSize.value,
                variantColor.value,
                variant.price as String,
                variantImage as String
            )
        }
    }

}