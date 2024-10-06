package com.iti4.retailhub.userlocalprofiledata

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject

class UserLocalProfileData (context: Context): UserLocalProfileDataInterface {

    private val profileData: SharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    private val profileDataEditor: SharedPreferences.Editor = profileData.edit()
    private val userShopID: SharedPreferences = context.getSharedPreferences("UserShopID", Context.MODE_PRIVATE)
    private val userShopIDEditor: SharedPreferences.Editor = userShopID.edit()

override fun addUserName(name:String):Int {
    if (name.isEmpty()) {
        return 0
    }else {
        profileDataEditor.putString("Name", name)
        profileDataEditor.apply()
        return 1
    }
}
    override fun addUserShopLocalId(id: String?) {
        profileDataEditor.putString("ID", id)
        profileDataEditor.apply()
    }
    override fun getUserShopLocalId(): String? {
        return profileData.getString("ID", null)
    }
    override fun addUserData(userID: String):Int {
            profileDataEditor.putString("UserID", userID)
            profileDataEditor.apply()
            return 1
    }
    override fun getUserProfileData(): String {
      val name= profileData.getString("Name", null)
        val userID= profileData.getString("UserID", null)
        val email= profileData.getString("Email", null)
        val profilePictureURl= profileData.getString("ProfilePictureURL", null)
        return "$name,$userID,$email,$profilePictureURl"
    }
    override fun deleteUserData() {
        profileDataEditor.clear()
        profileDataEditor.apply()
    }
}