package com.iti4.retailhub.features.address

data class PlaceLocation(
    val address: Address,
    val display_name: String,
)

data class Address(
    val house_number: String,
    val road: String,
    val hamlet: String,
    val city: String,
    val state: String,
    val postcode: String,
    val country: String,
    val country_code: String
)