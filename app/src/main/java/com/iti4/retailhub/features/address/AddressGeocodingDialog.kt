package com.iti4.retailhub.features.address

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.iti4.retailhub.databinding.DialogAddressLayoutBinding

class AddressGeocodingDialog(
    context: Context, actionHandler: OnClickMap
) : Dialog(context) {
    private val binding = DialogAddressLayoutBinding.inflate(LayoutInflater.from(context))


    init {
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        binding.btnYes.setOnClickListener {
            this.dismiss()
            actionHandler.navigateToDetails()
        }
        binding.btnNo.setOnClickListener {
            this.dismiss()
        }
    }

    fun getData(data: PlaceLocation) {
        binding.tvAddressName.text = data.display_name
    }


}