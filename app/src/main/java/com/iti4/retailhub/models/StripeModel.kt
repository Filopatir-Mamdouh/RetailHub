package com.iti4.retailhub.features.summary

data class Customer(
    val id: String,
    val email: String?,
    val name: String?,
    val phone: String?,
)

data class PaymentRequest(
    val email: String,
    val name:String,
    val amount: Int,
    val currency: String,
)

data class PaymentIntentResponse(
    val clientSecret: String,
    val dpmCheckerLink: String,
    val ephemeralKey: String,
    val customerId: String
)