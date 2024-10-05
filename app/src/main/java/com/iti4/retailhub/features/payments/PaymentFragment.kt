//package com.iti4.retailhub.features.payments
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import com.iti4.retailhub.databinding.FragmentPaymentBinding
//import com.iti4.retailhub.datastorage.network.ApiState
//import com.stripe.android.paymentsheet.PaymentSheet
//import com.stripe.android.paymentsheet.PaymentSheetResult
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//
//@AndroidEntryPoint
//class PaymentFragment : Fragment() {
//    private lateinit var binding: FragmentPaymentBinding
//    private val viewModel by viewModels<PaymentViewModel>()
//    private lateinit var customerConfig: PaymentSheet.CustomerConfiguration
//    private lateinit var paymentIntentClientSecret: String
//    private lateinit var paymentSheet: PaymentSheet
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentPaymentBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
//        initConfiguration()
//        viewModel.createPaymentIntent()
//        binding.btnPay.setOnClickListener {
//            presentPaymentSheet()
//        }
//    }
//
//    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
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
//    private fun initConfiguration() {
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
//                viewModel.paymentIntentResponse.collect { item ->
//                    when (item) {
//                        is ApiState.Success<*> -> {
//                            val paymentIntentResponse = item.data as PaymentIntentResponse
//                            customerConfig = PaymentSheet.CustomerConfiguration(
//                                paymentIntentResponse.customerId,
//                                paymentIntentResponse.ephemeralKey
//                            )
//                            paymentIntentClientSecret = paymentIntentResponse.clientSecret
//
//
//                        }
//
//                        is ApiState.Error -> {}
//                        is ApiState.Loading -> {}
//                    }
//                }
//            }
//        }
//
//    }
//
//    private fun presentPaymentSheet() {
//        paymentSheet.presentWithPaymentIntent(
//            paymentIntentClientSecret,
//            PaymentSheet.Configuration(
//                merchantDisplayName = "RetailHub",
//                customer = customerConfig
//            )
//        )
//    }
//}
