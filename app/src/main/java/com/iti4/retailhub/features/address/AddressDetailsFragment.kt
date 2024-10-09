package com.iti4.retailhub.features.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentAddressDetailsBinding
import com.iti4.retailhub.models.CustomerAddress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressDetailsFragment : Fragment() {
    private val viewModel: AddressViewModel by activityViewModels()
    private lateinit var binding: FragmentAddressDetailsBinding
    private var details: CustomerAddress? = null
    private lateinit var mapAddress: PlaceLocation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reason = arguments?.getString("reason")

        binding.btnSaveAddress.setOnClickListener {
            saveAddress()
            if (reason == "map") {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.addressFragment, true)
                    .build()
                findNavController().navigate(R.id.addressFragment, null, navOptions)
            } else
                findNavController().navigateUp()
        }
        when (reason) {
            "new" -> {

                // binding.tvTitle.text = "Add New Address"
            }

            "edit" -> {
                details = arguments?.getParcelable<CustomerAddress>("data") as CustomerAddress
                fillDataFromEdit()
            }

            "map" -> {
                mapAddress = viewModel.selectedMapAddress!!
                fillDataFromMap()
            }
        }
    }

    private fun saveAddress() {
        if (details == null){
            details = CustomerAddress(
                binding.etAddress.text.toString(),
                binding.etAppartment.text.toString() + "," + binding.etCity.text.toString() + "," + binding.etCountry.text.toString(),
                binding.etPhone.text.toString(), binding.etFullName.text.toString(), true,
            )
            details!!.id=details.hashCode().toString()
        }
        else {
            details!!.name = binding.etFullName.text.toString()
            details!!.address1 = binding.etAddress.text.toString()
            details!!.address2 =
                binding.etAppartment.text.toString() + "," + binding.etCity.text.toString() + "," + binding.etCountry.text.toString()
            details!!.phone = binding.etPhone.text.toString()
            details!!.newAddress = false
        }
        viewModel.addAddress(details!!)


    }


    private fun fillDataFromEdit() {
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

    private fun fillDataFromMap() {
        binding.apply {
            val address = mapAddress.address
            val address1 =
                ( if (address.house_number != "null") address.house_number else "") + " " +(if (address.road != "null") address.road else "")
            etAddress.setText(address1)
            etCity.setText(address.city)
            etCountry.setText(address.country)
        }
    }


}