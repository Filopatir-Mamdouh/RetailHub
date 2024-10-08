package com.iti4.retailhub.modelsdata


data class PlaceLocation(
    val lat: String,
    val lon: String,
    val address: Address
)

data class Address(
    val name: String,
    val state: String?=null,
    val postcode: String?=null,
    val country: String,
    val country_code: String? = null,
    val road: String? = null,
    val neighbourhood: String? = null,
    val city: String? = null,
    val suburb: String? = null
)
