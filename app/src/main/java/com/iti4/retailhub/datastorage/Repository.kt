package com.iti4.retailhub.datastorage

import com.iti4.retailhub.CompleteDraftOrderMutation
import com.iti4.retailhub.CreateDraftOrderMutation
import com.iti4.retailhub.DeleteDraftOrderMutation
import com.iti4.retailhub.DraftOrderInvoiceSendMutation
import com.iti4.retailhub.GetCustomerByIdQuery
import com.iti4.retailhub.UpdateDraftOrderMutation
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import com.iti4.retailhub.datastorage.network.RetrofitDataSource
import com.iti4.retailhub.features.payments.PaymentRequest
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.DraftOrderInputModel
import com.iti4.retailhub.models.HomeProducts
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val retrofitDataSource: RetrofitDataSource
) : IRepository {


    override fun getProducts(query: String): Flow<List<HomeProducts>> {
        return remoteDataSource.getProducts(query)
    }

    override fun getBrands(): Flow<List<Brands>> {
        return remoteDataSource.getBrands()
    }

    override fun getMyBagProducts(query: String): Flow<List<CartProduct>> {
        return remoteDataSource.getMyBagProducts(query)
    }

    override fun deleteMyBagItem(query: String): Flow<DeleteDraftOrderMutation.DraftOrderDelete> {
        return remoteDataSource.deleteMyBagItem(query)
    }

    override fun updateMyBagItem(cartProduct: CartProduct): Flow<UpdateDraftOrderMutation.DraftOrderUpdate> {
        return remoteDataSource.updateMyBagItem(cartProduct)
    }

    override fun createStripePaymentIntent(paymentRequest: PaymentRequest): Flow<Response<ResponseBody>> {
        return retrofitDataSource.createStripePaymentIntent(paymentRequest)
    }

    override fun getCustomerInfoById(id: String): Flow<GetCustomerByIdQuery.Customer> {
        return remoteDataSource.getCustomerInfoById(id)
    }

    override fun createCheckoutDraftOrder(draftOrderInputModel: DraftOrderInputModel): Flow<CreateDraftOrderMutation.DraftOrderCreate> {
        return remoteDataSource.createCheckoutDraftOrder(draftOrderInputModel)
    }
    override fun emailCheckoutDraftOrder(draftOrderId:String ): Flow<DraftOrderInvoiceSendMutation.DraftOrder> {
        return remoteDataSource.emailCheckoutDraftOrder(draftOrderId)
    }

    override fun completeCheckoutDraftOrder(draftOrderId:String ): Flow<CompleteDraftOrderMutation.DraftOrder> {
        return remoteDataSource.completeCheckoutDraftOrder(draftOrderId)
    }


}