package com.iti4.retailhub.features.home

import com.iti4.retailhub.models.Discount

interface OnClickGoToDetails {
    fun goToDetails(productId: String)
    fun saveToFavorites(
        variantID: String, productId: String,
        selectedProductColor: String,
        selectedProductSize: String,
        productTitle: String,
        selectedImage: String,
        price: String
    )

}