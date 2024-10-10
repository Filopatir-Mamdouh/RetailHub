package com.iti4.retailhub.models

data class Order(
    val id: String,
    val name: String,
    val number: String?,
    val dateTime: String,
    val price: String,
    val currency: String,
    val quantity: Int,
    val financialStatus: String,
    val fulfillmentStatus: String
)
