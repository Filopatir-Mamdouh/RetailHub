package com.iti4.retailhub.features.mybag

import java.io.Serializable

data class Product(
    var title: String,
    var price: Double,
    var color: String,
    var size: String,
    var image: Int,
    var quantity:Int
) : Serializable {
}

data class Promo(
    var title: String,
    var code: String,
    var remainingTime: String,
) : Serializable {
}