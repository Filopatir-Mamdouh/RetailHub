package com.iti4.retailhub.userlocalprofiledata

interface UserLocalProfileDataInterface {
    fun addUserName(name:String):Int
    fun addUserData(userID: String):Int
    fun getUserProfileData(): String
    fun deleteUserData()
     fun addUserShopLocalId(id: String?)
    fun getUserShopLocalId(): String?
}