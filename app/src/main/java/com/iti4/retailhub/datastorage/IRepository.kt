package com.iti4.retailhub.datastorage


import android.content.Intent
import android.content.IntentSender
import com.apollographql.apollo.api.Optional
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.iti4.retailhub.CompleteDraftOrderMutation
import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.CreateDraftOrderMutation
import com.iti4.retailhub.CustomerEmailSearchQuery
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.DraftOrderInvoiceSendMutation
import com.iti4.retailhub.GetAddressesByIdQuery
import com.iti4.retailhub.GetCustomerByIdQuery
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.MarkAsPaidMutation
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.UpdateCustomerAddressesMutation
import com.iti4.retailhub.UpdateCustomerFavoritesMetafieldsMutation
import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.features.summary.PaymentRequest
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.Category
import com.iti4.retailhub.models.CustomerAddress
import com.iti4.retailhub.models.DraftOrderInputModel
import com.iti4.retailhub.models.HomeProducts
import com.iti4.retailhub.models.Order
import com.iti4.retailhub.models.OrderDetails
import com.iti4.retailhub.models.Review
import com.iti4.retailhub.modelsdata.PlaceLocation
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.type.MetafieldDeleteInput
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface IRepository {

    fun getMyBagProducts(query: String): Flow<List<CartProduct>>
    fun deleteMyBagItem(query: String): Flow<DeleteDraftOrderMutation.DraftOrderDelete>
    fun updateMyBagItem(cartProduct: CartProduct): Flow<UpdateDraftOrderMutation.DraftOrderUpdate>
    fun createStripePaymentIntent(paymentRequest: PaymentRequest): Flow<Response<ResponseBody>>
    fun getCustomerInfoById(id: String): Flow<GetCustomerByIdQuery.Customer>
    fun getProducts(query: String): Flow<List<HomeProducts>>
    fun getBrands(): Flow<List<Brands>>

    fun getProductTypesOfCollection(): Flow<List<Category>>
    fun createCheckoutDraftOrder(draftOrderInputModel: DraftOrderInputModel): Flow<CreateDraftOrderMutation.DraftOrderCreate>
    fun emailCheckoutDraftOrder(draftOrderId: String): Flow<DraftOrderInvoiceSendMutation.DraftOrder>
    fun completeCheckoutDraftOrder(draftOrderId: String): Flow<CompleteDraftOrderMutation.DraftOrder>
    fun markOrderAsPaid(orderId: String): Flow<MarkAsPaidMutation.OrderMarkAsPaid>

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
    fun getAllReviews(reviewsNumbers: Int): List<Review>
    fun insertMyBagItem(
        varientId: String,
        customerId: String
    ): Flow<CreateDraftOrderMutation.DraftOrderCreate>

    fun GetDraftOrdersByCustomer(varientId: String): Flow<GetDraftOrdersByCustomerQuery.DraftOrders>

    fun getAddressesById(customerId: String): Flow<GetAddressesByIdQuery.Customer>
    fun updateCustomerAddress(
        customerId: String,
        address: List<CustomerAddress>
    ): Flow<UpdateCustomerAddressesMutation.CustomerUpdate>

    fun getLocationSuggestions(query: String): Flow<Response<List<PlaceLocation>>>
    fun getLocationGeocoding(
        lat: String,
        lon: String
    ): Flow<Response<com.iti4.retailhub.features.address.PlaceLocation>>
    fun saveProductToFavotes(input: CustomerInput): Flow<UpdateCustomerFavoritesMetafieldsMutation.CustomerUpdate>
    fun getCustomerFavoritesoById(id: String): Flow<GetCustomerFavoritesQuery.Customer>
    fun deleteCustomerFavoritItem(id: MetafieldDeleteInput): Flow<String?>
    fun getOrders(query: String): Flow<List<Order>>
    fun getOrderDetails(id: String): Flow<OrderDetails>
}