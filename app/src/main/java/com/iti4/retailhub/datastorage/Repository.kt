package com.iti4.retailhub.datastorage

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.CustomerEmailSearchQuery
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import com.iti4.retailhub.datastorage.reviews.ReviewsDataStoreInterface
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.userauthuntication.UserAuthunticationInterface
import com.iti4.retailhub.userlocalprofiledata.UserLocalProfileDataInterface
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import com.iti4.retailhub.models.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class Repository @Inject constructor(private val remoteDataSource: RemoteDataSource,
    private val userAuthuntication: UserAuthunticationInterface,
    private val UserLocalProfileData: UserLocalProfileDataInterface,
    private val reviewsDataStore: ReviewsDataStoreInterface
) : IRepository {

    override fun getProducts(query: String) : Flow<List<HomeProducts>> {
        return remoteDataSource.getProducts(query)
    }
    override fun getBrands() : Flow<List<Brands>> {
        return remoteDataSource.getBrands()
    }
    override fun createUser(input: CustomerInput) : Flow<CreateCustomerMutation.CustomerCreate>{
        return remoteDataSource.createUser(input)
    }



    override suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult? {
        return userAuthuntication.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult? {
        return userAuthuntication.signInWithEmailAndPassword(email, password)
    }
    override suspend fun loginOut():Boolean {
        return userAuthuntication.loginOut()
    }
    override suspend fun sendEmailVerification(user: FirebaseUser): Boolean {
       return userAuthuntication.sendEmailVerification(user)
    }
    override  suspend fun signIn(): IntentSender? {
        return userAuthuntication.signIn()
    }

    override suspend fun signInWithIntent(intent: Intent): AuthResult? {
        return userAuthuntication.signInWithIntent(intent)
    }

    override fun addUserName(name:String):Int{
        return UserLocalProfileData.addUserName(name)
    }
    override fun addUserData(userID: String):Int{
        return UserLocalProfileData.addUserData(userID)
    }
    override fun getUserProfileData(): String{
        return UserLocalProfileData.getUserProfileData()
    }
    override fun deleteUserData(){
        UserLocalProfileData.deleteUserData()
    }

    override fun addUserShopLocalId(id: String?) {
        UserLocalProfileData.addUserShopLocalId(id)
    }
    override fun getUserShopLocalId(): String? {
      return  UserLocalProfileData.getUserShopLocalId()
    }
    override fun getCustomerIdByEmail(email: String): Flow<CustomerEmailSearchQuery.Customers>{
        return remoteDataSource.getCustomerIdByEmail(email)
    }
    override  fun getProductDetails(id: String): Flow<ProductDetailsQuery.OnProduct?>{
        return remoteDataSource.getProductDetails(id)
    }

    override fun addReview(review: Review) {
        reviewsDataStore.addReview(review)
    }

    override  fun getAllReviews(reviewsNumbers:Int): List<Review> {
        return reviewsDataStore.getAllReviews(reviewsNumbers)
    }
}