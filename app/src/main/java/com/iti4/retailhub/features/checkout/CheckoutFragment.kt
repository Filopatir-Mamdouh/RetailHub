package com.iti4.retailhub.features.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iti4.retailhub.databinding.FragmentCheckoutBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.payments.PaymentIntentResponse
import com.iti4.retailhub.models.CartProduct
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutFragment : Fragment(), Communicator {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var cartProducts: List<CartProduct>
    private lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    private lateinit var paymentIntentClientSecret: String
    private lateinit var paymentSheet: PaymentSheet

    private val viewModel by viewModels<CheckoutViewModel>()
    private var totalPrice: Double? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartProducts =
            arguments?.getParcelableArrayList<CartProduct>("data") as MutableList<CartProduct>
        totalPrice = arguments?.getDouble("totalprice")
        binding.tvSummary.text = totalPrice.toString()

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        initConfiguration()
        viewModel.createPaymentIntent()
        binding.btnSubmitOrder.setOnClickListener {
            Log.i("here", "onViewCreated: hello ")
            presentPaymentSheet()
        }
        binding.promocodeEdittext.btnInsertCode.setOnClickListener {
            val bottomSheet = MyBottomSheetFragment(this)
            bottomSheet.show(this.requireActivity().supportFragmentManager, bottomSheet.tag)
        }
        binding.promocodeEdittext.btnDeleteCode.setOnClickListener {
            binding.promocodeEdittext.btnInsertCode.visibility = View.VISIBLE
            binding.promocodeEdittext.btnDeleteCode.visibility = View.GONE
            binding.promocodeEdittext.etPromoCode.setText("")
            binding.promocodeEdittext.etPromoCode.isFocusableInTouchMode = false
            binding.promocodeEdittext.etPromoCode.isFocusable = false
        }

        binding.promocodeEdittext.etPromoCode.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding.promocodeEdittext.etPromoCode.isFocusableInTouchMode = true
                binding.promocodeEdittext.etPromoCode.isFocusable = true
                val bottomSheet = MyBottomSheetFragment(this)
                bottomSheet.show(this.requireActivity().supportFragmentManager, bottomSheet.tag)
                return@setOnTouchListener true
            }
            false
        }
    }

    override fun passData(data: String) {
        binding.promocodeEdittext.btnInsertCode.visibility = View.GONE
        binding.promocodeEdittext.btnDeleteCode.visibility = View.VISIBLE
        binding.promocodeEdittext.etPromoCode.setText(data)
    }


    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Log.i("here", "onPaymentSheetResult: Payment Completed ")
            }

            is PaymentSheetResult.Canceled -> {
                Log.i("here", "Payment canceled!")
            }

            is PaymentSheetResult.Failed -> {
                val error = paymentSheetResult.error
                Log.i("here", "Payment failed" + error.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun initConfiguration() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.paymentIntentResponse.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            val paymentIntentResponse = item.data as PaymentIntentResponse
                            customerConfig = PaymentSheet.CustomerConfiguration(
                                paymentIntentResponse.customerId,
                                paymentIntentResponse.ephemeralKey
                            )
                            paymentIntentClientSecret = paymentIntentResponse.clientSecret


                        }

                        is ApiState.Error -> {}
                        is ApiState.Loading -> {}
                    }
                }
            }
        }

    }

    private fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "RetailHub",
                customer = customerConfig
            )
        )
    }


}