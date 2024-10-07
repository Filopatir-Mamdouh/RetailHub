package com.iti4.retailhub.features.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iti4.retailhub.databinding.MybagbottomSheetLayoutBinding
import com.iti4.retailhub.features.mybag.Promo

class MyBottomSheetFragment(private val onClickBottomSheet: OnClickBottomSheet) : BottomSheetDialogFragment(),
    OnClickApply {
    private lateinit var binding: MybagbottomSheetLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MybagbottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.promocodeEdittext.etPromoCode.isFocusableInTouchMode = true
        binding.promocodeEdittext.etPromoCode.isFocusable = true

        binding.promocodeEdittext.btnInsertCode.setOnClickListener {
            if (binding.promocodeEdittext.etPromoCode.text.isNotEmpty()) {
                onClickBottomSheet.passData(binding.promocodeEdittext.etPromoCode.text.toString())
                dismiss()
            }
        }

        val manager = LinearLayoutManager(requireContext())
        manager.setOrientation(RecyclerView.VERTICAL)
        binding.bottomSheetRecyclerView.layoutManager = manager

        val adapter =
            MyBagCouponRecyclerViewAdapter(
                this
            )
        binding.bottomSheetRecyclerView.adapter = adapter

        adapter.submitList(
            listOf(
                Promo("Personal Offer", "mypromocode2020", "6 days remaining "),
                Promo("Summer Sale", "summer2020", "23 days remaining "),
                Promo("Personal Offer", "mypromocode2020", "7 days remaining "),
                Promo("Personal Offer", "mypromocode2020", "7 days remaining "),
                Promo("Summer Sale", "summer2020", "23 days remaining "),
                Promo("Personal Offer", "mypromocode2020", "7 days remaining "),
                Promo("Summer Sale", "summer2020", "23 days remaining "),
                Promo("Personal Offer", "mypromocode2020", "7 days remaining "),
                Promo("Summer Sale", "summer2020", "23 days remaining "),
                Promo("Summer Sale", "summer2020", "23 days remaining "),
            )
        )
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet =
            dialog?.findViewById<View>(R.id.design_bottom_sheet)

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isFitToContents = false
            behavior.halfExpandedRatio = 0.7f
            behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            behavior.isDraggable = false
        }
    }

    override fun onClickApply(promo: Promo) {
        binding.promocodeEdittext.etPromoCode.setText(promo.code)
    }


}