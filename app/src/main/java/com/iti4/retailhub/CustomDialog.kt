package com.iti4.retailhub

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable

import android.view.LayoutInflater
import com.iti4.retailhub.databinding.DialogConfirmationLayoutBinding
import com.iti4.retailhub.features.address.OnClickAddress


class CustomDialog(
    context: Context,
    private val actionHandler: OnClickAddress
) : Dialog(context) {
    private val binding = DialogConfirmationLayoutBinding.inflate(LayoutInflater.from(context))
    private lateinit var data: String

    init {
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        binding.btnYes.setOnClickListener {
            this.dismiss()
            if(data!="null")
            actionHandler.deleteAddress(data)
        }
        binding.btnNo.setOnClickListener {
            this.dismiss()
        }
    }

    fun getData(id: String) {
        data = id
    }


}