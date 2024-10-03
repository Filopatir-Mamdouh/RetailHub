package com.iti4.retailhub.userlocalprofiledata

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class UserLocalProfileData private constructor(context: Context) {

    private val profileData: SharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    private val profileDataEditor: SharedPreferences.Editor = profileData.edit()
  /*  private val userName: SharedPreferences = context.getSharedPreferences("UserName", Context.MODE_PRIVATE)
    private val userNameEditor: SharedPreferences.Editor = userName.edit()*/

    companion object {
        private var instance: UserLocalProfileData? = null

        @Synchronized
        fun getInstance(context: Context): UserLocalProfileData {
            return instance ?: UserLocalProfileData(context).also { instance = it }
        }
    }
fun addUserName(name:String):Int {
    if (name.isEmpty()) {
        return 0
    }else {
        profileDataEditor.putString("Name", name)
        profileDataEditor.apply()
        return 1
    }
}
    fun addUserData(userID: String, name: String, email: String, profilePictureURl: String):Int {
        Log.d("UserLocalProfileData", "addUserData called with userID: $userID, name: $name, email: $email, profilePictureURl: $profilePictureURl")
        if (email.isEmpty() || profilePictureURl.isEmpty()|| userID.isEmpty()) {
            return 0
        }else {
            if (name.isBlank()||name=="null"||name.isEmpty()) {
                profileDataEditor.putString("Name", profileData.getString("Name", ""))
            }else{
                profileDataEditor.putString("Name", name)
            }
            profileDataEditor.putString("UserID", userID)
            profileDataEditor.putString("Email", email)
            profileDataEditor.putString("ProfilePictureURL", profilePictureURl)
            profileDataEditor.apply()
            return 1
        }
    }
    fun getUserProfileData(): String {
      val name= profileData.getString("Name", null)
        val userID= profileData.getString("UserID", null)
        val email= profileData.getString("Email", null)
        val profilePictureURl= profileData.getString("ProfilePictureURL", null)
        return "$name,$userID,$email,$profilePictureURl"
    }
    fun deleteUserData() {
        profileDataEditor.clear()
        profileDataEditor.apply()
    }
}