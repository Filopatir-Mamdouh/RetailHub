package com.iti4.retailhub.features.address

import com.iti4.retailhub.models.CustomerAddress

interface OnClickAddress {
    fun saveAddressDetails(address : CustomerAddress)
    fun editDetails(address : CustomerAddress)
}