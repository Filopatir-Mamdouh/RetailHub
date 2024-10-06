package com.iti4.retailhub.features.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iti4.retailhub.R
import com.iti4.retailhub.communicators.ToolbarController
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
    private lateinit var appearance: PaymentSheet.Appearance

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
        initConfiguration()

        binding.btnSubmitOrder.setOnClickListener {
            if (binding.radioGroupPaymentMethod.checkedRadioButtonId != -1) {
                when (view.findViewById<RadioButton>(binding.radioGroupPaymentMethod.checkedRadioButtonId).text.toString()) {
                    getString(R.string.CashOnDelivery) -> {
                        viewModel.createCheckoutDraftOrder(cartProducts)

                    }
                    getString(R.string.PayWithCard) -> {
                        viewModel.createPaymentIntent()
                    }
                }
            }
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
                            initPaymentSheetAppearance()
                            presentPaymentSheet()
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
                customer = customerConfig,
                appearance = appearance
            )
        )
    }

    private fun initPaymentSheetAppearance() {
        appearance = PaymentSheet.Appearance(
            colorsLight = PaymentSheet.Colors(
                primary = resources.getColor(R.color.red_color),
                component = resources.getColor(R.color.white),
                surface = resources.getColor(R.color.background_color),
                componentBorder = resources.getColor(R.color.transparent),
                componentDivider = resources.getColor(R.color.black_variant),
                onComponent = resources.getColor(R.color.black_variant),
                subtitle = resources.getColor(R.color.black_variant),
                placeholderText = resources.getColor(R.color.grey),
                onSurface = resources.getColor(R.color.black_variant),
                appBarIcon = resources.getColor(R.color.black_variant),
                error = resources.getColor(R.color.red_color),
            ),
            shapes = PaymentSheet.Shapes(
                cornerRadiusDp = 12.0f,
                borderStrokeWidthDp = 0.5f
            ),
            typography = PaymentSheet.Typography.default.copy(
                fontResId = R.font.metropolis_regular,
                sizeScaleFactor = 1.10f
            ),
            primaryButton = PaymentSheet.PrimaryButton(
                shape = PaymentSheet.PrimaryButtonShape(
                    cornerRadiusDp = 20f
                ),
            )
        )
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as ToolbarController).apply {
            setVisibility(true)
            setTitle("Checkout")
        }
    }


}