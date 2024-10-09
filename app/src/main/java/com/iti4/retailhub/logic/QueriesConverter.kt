package com.iti4.retailhub.logic

import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.GetProductTypesOfCollectionQuery
import com.iti4.retailhub.OrdersQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.Category
import com.iti4.retailhub.models.HomeProducts
import com.iti4.retailhub.models.Order

fun ProductsQuery.Products.toProductsList(): List<HomeProducts> {
    val list = ArrayList<HomeProducts>()
    this.edges.forEach {
        list.add(HomeProducts(
            it.node.id,
            it.node.title,
            it.node.totalInventory,
            it.node.media.nodes[0].onMediaImage?.image?.url.toString(),
            it.node.vendor,
            it.node.priceRangeV2.maxVariantPrice.amount.toString(),
            it.node.priceRangeV2.minVariantPrice.amount.toString(),
            it.node.priceRangeV2.minVariantPrice.currencyCode.toString())
        )
    }
    return list
}

fun CollectionsQuery.Collections.toBrandsList(): List<Brands> {
    val list = ArrayList<Brands>()
    this.nodes.forEach {
        list.add(Brands(it.id, it.title, it.image?.url.toString()))
    }
    list.removeFirst()
    return list
}

fun GetProductTypesOfCollectionQuery.Node.toCategory() : Category{
    return Category(this.id, this.title, this.products.nodes.distinct().map { it.productType })
}

fun OrdersQuery.Node.toOrder(): Order {
    return Order(
        this.id,
        this.name,
        this.confirmationNumber,
        this.createdAt.toString().split("T")[0],
        this.currentTotalPriceSet.shopMoney.amount.toString(),
        this.currentTotalPriceSet.shopMoney.currencyCode.toString(),
        this.currentSubtotalLineItemsQuantity,
        this.displayFinancialStatus.toString(),
        this.displayFulfillmentStatus.toString()
    )
}