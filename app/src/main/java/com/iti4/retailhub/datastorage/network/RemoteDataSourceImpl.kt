package com.iti4.retailhub.datastorage.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.GetProductTypesOfCollectionQuery
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.logic.toBrandsList
import com.iti4.retailhub.logic.toCategory
import com.iti4.retailhub.logic.toProductsList
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.Category
import com.iti4.retailhub.models.HomeProducts
import com.iti4.retailhub.type.DraftOrderDeleteInput
import com.iti4.retailhub.type.DraftOrderInput
import com.iti4.retailhub.type.DraftOrderLineItemInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RemoteDataSourceImpl @Inject constructor(private val apolloClient: ApolloClient) :
    RemoteDataSource {
    override fun getProducts(query: String): Flow<List<HomeProducts>> = flow {
        val response = apolloClient.query(ProductsQuery(query)).execute()
        if (!response.hasErrors() && response.data != null) {
            emit(response.data!!.products.toProductsList())
        } else {
            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
        }
    }

    override fun getBrands(): Flow<List<Brands>> = flow {
        val response = apolloClient.query(CollectionsQuery()).execute()
        if (!response.hasErrors() && response.data != null) {
            emit(response.data!!.collections.toBrandsList())
        } else {
            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
        }
    }

    override fun getProductTypesOfCollection(): Flow<List<Category>> = flow {
        val response = apolloClient.query(GetProductTypesOfCollectionQuery()).execute()
        if (!response.hasErrors() && response.data != null) {
            val list = ArrayList<Category>()
            response.data!!.collections.nodes.forEach {
                list.add(it.toCategory())
            }
            list.removeFirst()
            list.reverse()
            emit(list)
        } else {
            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
        }
    }

    override fun getOrders(query: String): Flow<OrdersQuery.Orders> = flow {
        val response = apolloClient.query(OrdersQuery(query)).execute()
        if (!response.hasErrors() && response.data != null) {
            emit(response.data!!.orders)
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