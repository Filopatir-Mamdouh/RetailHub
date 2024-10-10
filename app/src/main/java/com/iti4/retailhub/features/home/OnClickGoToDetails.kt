package com.iti4.retailhub.features.home

interface OnClickGoToDetails {
    fun goToDetails(productId : String)
     fun saveToFavorites(
        variantID: String,productId:String,
        selectedProductColor: String,
        selectedProductSize: String,
        productTitle: String,
        selectedImage: String,
        price: String
    )
}