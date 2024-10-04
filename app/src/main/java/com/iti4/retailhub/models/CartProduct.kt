package com.iti4.retailhub.models

data class CartProduct(
    val draftOrderId: String, val itemId: String,
    var itemQuantity: Int, val inventoryQuantity: Int, val itemTitle: String,
    val itemSize: String, val itemColor: String,
    val itemPrice: String, val itemImage: String
)