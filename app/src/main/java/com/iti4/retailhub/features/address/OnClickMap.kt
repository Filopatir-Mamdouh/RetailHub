package com.iti4.retailhub.features.address

import com.iti4.retailhub.modelsdata.PlaceLocation

interface OnClickMap {
    fun goToAddress(location: PlaceLocation)
    fun navigateToDetails()
}