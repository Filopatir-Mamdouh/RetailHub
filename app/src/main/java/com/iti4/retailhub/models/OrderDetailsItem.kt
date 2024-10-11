package com.iti4.retailhub.models

data class OrderDetailsItem (
    val id: String,
    val title:String,
    val brand: String,
    val quantity: String,
    val size: String,
    val color: String,
    val price: String,
    val currency: String,
    val image: String
)
