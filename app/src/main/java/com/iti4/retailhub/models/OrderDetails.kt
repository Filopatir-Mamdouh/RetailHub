package com.iti4.retailhub.models

data class OrderDetails(
    val id: String,
    val name: String,
    val date: String,
    val number: String,
    val status: String,
    val quantity: String,
    val items: List<OrderDetailsItem>,
    val shippingAddress: String,
    val financialStatus: String,
    val discount: String,
    val total: String,
    val currency: String
)
