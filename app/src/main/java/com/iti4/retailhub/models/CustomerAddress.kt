package com.iti4.retailhub.models

import android.os.Parcel
import android.os.Parcelable


data class CustomerAddress(
    var address1: String, var address2: String,
    var phone: String, var name: String,
    var newAddress: Boolean = false, var id:String?=null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address1)
        parcel.writeString(address2)
        parcel.writeString(phone)
        parcel.writeString(name)
        parcel.writeByte(if (newAddress) 1 else 0)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartProduct> {
        override fun createFromParcel(parcel: Parcel): CartProduct {
            return CartProduct(parcel)
        }

        override fun newArray(size: Int): Array<CartProduct?> {
            return arrayOfNulls(size)
        }
    }

}
