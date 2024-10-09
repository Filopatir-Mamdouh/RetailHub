package com.iti4.retailhub.models


data class CurrencyResponse(
    val result: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>
)

enum class CountryCodes {
    EGP, USD, EUR, AED
}

