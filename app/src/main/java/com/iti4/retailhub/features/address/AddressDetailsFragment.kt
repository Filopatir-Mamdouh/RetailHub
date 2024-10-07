package com.iti4.retailhub.features.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iti4.retailhub.databinding.FragmentAddressDetailsBinding
import com.iti4.retailhub.models.CustomerAddress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAddressDetailsBinding
    private var details: CustomerAddress? = null
    private var newAddress: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSaveAddress.setOnClickListener{
            saveAddress()
        }
        val reason = arguments?.getString("reason")
        when (reason) {
            "new" -> {
                newAddress = true
                // binding.tvTitle.text = "Add New Address"
            }

            "edit" -> {
                details = arguments?.getParcelable<CustomerAddress>("data") as CustomerAddress
                fillData()
            }
        }
    }

    private fun saveAddress() {
        if (details == null)
            details = CustomerAddress(
                binding.etAddress.text.toString(),
                binding.etAppartment.text.toString() + "," + binding.etCity.text.toString() + "," + binding.etCountry.text.toString(),
                binding.etPhone.text.toString(), binding.etFullName.text.toString(), true
            )
        else {
            details!!.name = binding.etFullName.text.toString()
            details!!.address1 = binding.etAddress.text.toString()
            details!!.address2 =
                binding.etAppartment.text.toString() + "," + binding.etCity.text.toString() + "," + binding.etCountry.text.toString()
            details!!.phone = binding.etPhone.text.toString()
            details!!.newAddress = false
        }


    }


    private fun fillData() {
        binding.apply {
            etFullName.setText(details!!.name)
            etPhone.setText(details!!.phone)
            etAddress.setText(details!!.address1)
            if (details!!.address2 != "empty") {
                val address2 = details!!.address2.split(",")
                etAppartment.setText(address2[0])
                etCity.setText(address2[1])
                etCountry.setText(address2[2])
            }
        }
    }


}