package com.iti4.retailhub.features.mybag

import com.iti4.retailhub.models.CartProduct

interface OnClickMyBag {
    fun deleteMyBagItem(itemId: String)
    fun updateTotalPrice()
}