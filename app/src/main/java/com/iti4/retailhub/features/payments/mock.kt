////package com.iti4.retailhub.features.payments
////
////import android.os.Bundle
////import android.util.Log
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import androidx.fragment.app.Fragment
////import androidx.fragment.app.viewModels
////import androidx.lifecycle.lifecycleScope
////import androidx.lifecycle.repeatOnLifecycle
////import com.iti4.retailhub.databinding.FragmentPaymentBinding
////import com.iti4.retailhub.datastorage.network.ApiState
////import com.stripe.android.paymentsheet.CreateIntentResult
////import com.stripe.android.paymentsheet.PaymentSheet
////import com.stripe.android.paymentsheet.PaymentSheetResult
////import com.stripe.android.paymentsheet.model.PaymentOption
////import dagger.hilt.android.AndroidEntryPoint
////import kotlinx.coroutines.launch
////
////@AndroidEntryPoint
////class PaymentFragment : Fragment() {
////    private lateinit var binding: FragmentPaymentBinding
////    private val viewModel by viewModels<PaymentViewModel>()
////    private var paymentIntentClientSecret: String? = null
////    private var paymentSheet: PaymentSheet? = null
////    private lateinit var flowController: PaymentSheet.FlowController
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View? {
////        binding = FragmentPaymentBinding.inflate(inflater, container, false)
////        return binding.root
////    }
////
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////
////
////        binding.btnPay.setOnClickListener {
////            //  viewModel.createPaymentIntent()
////            flowController = PaymentSheet.FlowController.create(
////                activity = this.requireActivity(),
////                paymentOptionCallback = ::onPaymentOption,
////                createIntentCallback = { _, _ ->
////                    // Make a request to your server to create a PaymentIntent and return its client secret
////                    try {
////                        val response = "pi_3Q6KKRB2VRlrbgQ708TQNQgc_secret_2x1Hur8hgLz5SYEZyzbXWYSwo"
////                        CreateIntentResult.Success("pi_3Q6LIlB2VRlrbgQ70niFLags_secret_XFiO6X0rg6AVuAu5oJnQr0oOA")
////                    } catch (e: Exception) {
////                        CreateIntentResult.Failure(
////                            cause = e,
////                            displayMessage = e.message
////                        )
////                    }
////                },
////                paymentResultCallback = ::onPaymentSheetResult,
////            )
////            flowController.configureWithPaymentIntent(
////                paymentIntentClientSecret = "your_payment_intent_secret",
////                configuration = PaymentSheet.Configuration(
////                    merchantDisplayName = "Your Merchant Name"
////                )
////            ) { flowControllerState, error ->
////                if (error != null) {
////                    // Handle error configuring the FlowController
////                    Log.e("StripeError", "Error configuring FlowController: ${error.message}")
////                } else {
////                    // FlowController successfully configured
////                    // Now you can call presentPaymentOptions() or presentPaymentSheet()
////                    flowController.presentPaymentOptions()
////                }
////            }
////            flowController.presentPaymentOptions()
////
////        }
//////        lifecycleScope.launch {
//////            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
//////                viewModel.paymentIntentResponse.collect { item ->
//////                    when (item) {
//////                        is ApiState.Success<*> -> {
//////                            paymentIntentClientSecret = item.data as String
//////                        }
//////                        is ApiState.Error -> {}
//////                        is ApiState.Loading -> {}
//////                    }
//////                }
//////            }
//////        }
//////        paymentSheet = PaymentSheet(this) { result ->
//////            viewModel.onPaymentSheetResult(result)
//////        };
////
//////        binding.btnAddress.setOnClickListener {
//////            viewModel.onPayClicked(paymentSheet!!, paymentIntentClientSecret!!)
//////            viewModel.createUser()
//////        }
////    }
////
////
////    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
////        when (paymentSheetResult) {
////            is PaymentSheetResult.Completed -> {
////                Log.i("here", "onPaymentSheetResult: Payment Completed ")
////            }
////
////            is PaymentSheetResult.Canceled -> {
////                Log.i("here", "Payment canceled!")
////            }
////
////            is PaymentSheetResult.Failed -> {
////                val error = paymentSheetResult.error
////                Log.i("here", "Payment failed" + error.localizedMessage ?: "Unknown error")
////            }
////        }
////    }
////
////
////    fun handleCheckoutLoaded(cartTotal: Long, currency: String) {
////        flowController.configureWithIntentConfiguration(
////            intentConfiguration = PaymentSheet.IntentConfiguration(
////                mode = PaymentSheet.IntentConfiguration.Mode.Payment(
////                    amount = cartTotal,
////                    currency = currency,
////                ),
////            ),
////            // Optional configuration - See the "Customize the sheet" section in this guide
////            configuration = PaymentSheet.Configuration(
////                merchantDisplayName = "Example Inc.",
////            ),
////            callback = { success, error ->
////                // If success, the FlowController was initialized correctly.
////                // Use flowController.getPaymentOption() to populate your payment
////                // method button.
////                flowController.getPaymentOption()
////            },
////        )
////    }
////
////    private fun onPaymentOption(paymentOption: PaymentOption?) {
////        if (paymentOption != null) {
////            binding.btnPay.text = paymentOption.label
////            binding.btnPay.setCompoundDrawablesRelativeWithIntrinsicBounds(
////                paymentOption.drawableResourceId,
////                0,
////                0,
////                0
////            )
////        } else {
////            binding.btnPay.text = "Select"
////            binding.btnPay.setCompoundDrawablesRelativeWithIntrinsicBounds(
////                null,
////                null,
////                null,
////                null
////            )
////        }
////    }
////}
////
////
////
//
//
//package com.iti4.retailhub.features.payments
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.iti4.retailhub.datastorage.IRepository
//import com.iti4.retailhub.datastorage.network.ApiState
//import com.stripe.android.paymentsheet.PaymentSheet
//import com.stripe.android.paymentsheet.PaymentSheetResult
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.onStart
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//import javax.inject.Inject
//
//@HiltViewModel
//class PaymentViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
//    private val dispatcher = Dispatchers.IO
//
//    private val _paymentIntentResponse = MutableStateFlow<ApiState>(ApiState.Loading)
//    val paymentIntentResponse = _paymentIntentResponse.onStart { createPaymentIntent() }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)
//
//
//    fun createUser() {
//        viewModelScope.launch(dispatcher) {
//            repository.createStripeUser().collect {
//                Log.i("here", "createUser: "+it)
//            }
//        }
//    }
//
//    fun createPaymentIntent() {
//        viewModelScope.launch(dispatcher) {
//            Log.i("here", "lunched: ")
//            repository.createStripePaymentIntent().collect {
//                it.body()
//                val response = it
//                Log.i("here", "response: "+it)
//                if (response.isSuccessful) {
//                    Log.i("here", "sucess: ")
//                    val responseBody = response.body()?.string()
//                    if (responseBody != null) {
//                        Log.i("here", "createPaymentIntent: "+responseBody.toString())
//                        val jsonObject = JSONObject(responseBody)
//                        _paymentIntentResponse.value =
//                            ApiState.Success(jsonObject.getString("clientSecret"))
//                    }
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.i("here", "createPaymentIntent: " + errorBody.toString())
//                }
//            }
//        }
//    }
//
//    fun onPayClicked(
//        paymentSheet: PaymentSheet,
//        paymentIntentClientSecret: String,
//    ) {
//        val configuration =
//            PaymentSheet.Configuration.Builder(merchantDisplayName = "Example, Inc.")
//                .build()
//
//        // Present Payment Sheet
//        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration)
//    }
//    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
//        when (paymentSheetResult) {
//            is PaymentSheetResult.Completed -> {
//                Log.i("here", "onPaymentSheetResult: Payment Completed ")
//            }
//
//            is PaymentSheetResult.Canceled -> {
//                Log.i("here", "Payment canceled!")
//            }
//
//            is PaymentSheetResult.Failed -> {
//                val error = paymentSheetResult.error
//                Log.i("here", "Payment failed" + error.localizedMessage ?: "Unknown error")
//            }
//        }
//    }
//
//}
//
//
//
//
