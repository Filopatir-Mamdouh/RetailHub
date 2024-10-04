package com.iti4.retailhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iti4.retailhub.databinding.FragmentCheckoutBinding
import com.iti4.retailhub.features.mybag.Communicator
import com.iti4.retailhub.features.mybag.MyBottomSheetFragment
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class CheckoutFragment : Fragment(), Communicator {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var paymentSheet: PaymentSheet
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

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
        // implemented in the next steps
    }

}