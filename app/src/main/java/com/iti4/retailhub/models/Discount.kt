package com.iti4.retailhub.models

data class Discount(val id: String, val value: String, val title: String) {
    fun getDiscountAsDouble(): Double {
        return if (value.endsWith("%")) {
            value.removeSuffix("%").toDouble()
        } else {
            0.0
        }
    }
}