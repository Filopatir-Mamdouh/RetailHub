package com.iti4.retailhub.features.mybag

import java.io.Serializable

data class Product(
    var title: String,
    var price: Double,
    var color: String,
    var size: String,
    var image: Int
) : Serializable {
}