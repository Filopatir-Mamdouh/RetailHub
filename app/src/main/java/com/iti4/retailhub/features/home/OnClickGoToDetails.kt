package com.iti4.retailhub.features.home

interface OnClickGoToDetails {
    fun goToDetails(productId : String)
    fun saveToFavorites(
        productId:String,
        productTitle: String,
        selectedImage: String,
        price: String
    )

     fun deleteFromCustomerFavorites(pinFavorite: String)
}