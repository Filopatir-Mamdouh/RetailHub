package com.iti4.retailhub.datastorage

import android.content.Intent
import android.content.IntentSender
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.CustomerEmailSearchQuery
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.type.CustomerInput
import kotlinx.coroutines.flow.Flow
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import com.iti4.retailhub.models.Review

interface IRepository {
    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>
    fun createUser(input: CustomerInput): Flow<CreateCustomerMutation.CustomerCreate>

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult?
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult?
    suspend fun loginOut(): Boolean
    suspend fun sendEmailVerification(user: FirebaseUser): Boolean
    suspend fun signIn(): IntentSender?
    suspend fun signInWithIntent(intent: Intent): AuthResult?


    fun addUserName(name: String): Int
    fun addUserData(userID: String): Int
    fun getUserProfileData(): String
    fun deleteUserData()
    fun addUserShopLocalId(id: String?)
    fun getUserShopLocalId(): String?
    fun getCustomerIdByEmail(email: String): Flow<CustomerEmailSearchQuery.Customers>
    fun getProductDetails(id: String): Flow<ProductDetailsQuery.OnProduct?>
    fun addReview(review: Review)
    fun getAllReviews(reviewsNumbers:Int): List<Review>
}