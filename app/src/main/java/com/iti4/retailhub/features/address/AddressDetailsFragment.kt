package com.iti4.retailhub.features.address

import android.os.Bundle
import android.util.Log
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
import com.iti4.retailhub.modelsdata.countries
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
            if (checkIfValidAddress()) {
                saveAddress()
                if (reason == "map") {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.addressDetailsFragment, true)
                        .build()
                    findNavController().navigate(R.id.addressFragment, null, navOptions)
                } else {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.addressDetailsFragment, true)
                        .build()
                    findNavController().navigate(R.id.addressFragment, null, navOptions)
                }
            }
        }
        when (reason) {
            "new" -> {
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
        if (details == null) {
            details = CustomerAddress(
                binding.etAddress.text.toString(),
                binding.etAppartment.text.toString() + "," + binding.etCity.text.toString() + "," + binding.etCountry.text.toString(),
                binding.etPhone.text.toString(), binding.etFullName.text.toString(), true,
            )
            details!!.id = details.hashCode().toString()
        } else {
            details!!.name = binding.etFullName.text.toString()
            details!!.address1 = binding.etAddress.text.toString()
            details!!.address2 =
                binding.etAppartment.text.toString() + "," + binding.etCity.text.toString() + "," + binding.etCountry.text.toString()
            details!!.phone = binding.etPhone.text.toString()
            details!!.newAddress = false
        }
        viewModel.addAddress(details!!)
    }

    private fun checkIfValidAddress(): Boolean {
        var validAddress = true
        binding.apply {
            if (etFullName.text.isEmpty()) {
                validAddress = false
                etFullName.error = resources.getString(R.string.ErrorName)
            }
            if (etAddress.text.isEmpty()) {
                validAddress = false
                etAddress.error = resources.getString(R.string.ErrorAddress)
            }
            if (etAppartment.text.isEmpty()) {
                validAddress = false
                etAppartment.error = resources.getString(R.string.ErrorApartment)
            }
            if (etCity.text.isEmpty()) {
                validAddress = false
                etCity.error = resources.getString(R.string.ErrorCity)
            }
            if (etCountry.text.isEmpty()) {
                validAddress = false
                etCountry.error = resources.getString(R.string.ErrorCountry)
            } else if (countries.none { it.equals(etCountry.text.toString(), true) }) {
                validAddress = false
                etCountry.error = resources.getString(R.string.ErrorCountry)
            }
            if (etPhone.text.isEmpty() || etPhone.text.length < 5) {
                validAddress = false
                etPhone.error = resources.getString(R.string.ErrorPhone)
            } else if (!etPhone.text.toString().all { it.isDigit() }) {
                validAddress = false
                etPhone.error = resources.getString(R.string.ErrorPhone)
            }
        }
        return validAddress
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
                (if (address.house_number != "null") address.house_number else "") + " " + (if (address.road != "null") address.road else "")
            etAddress.setText(address1)
            etCity.setText(address.city)
            etCountry.setText(address.country)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("here", "onDestroy: Details")
    }
}
