package com.iti4.retailhub.features.address

import com.iti4.retailhub.models.CustomerAddress

interface OnClickAddress {
    fun editDetails(address: CustomerAddress)
    fun deleteAddress(id: String)
    fun setDefaultAddress(addressIndex: Int)
    fun checkoutClickedAnAddress(address: CustomerAddress)
}