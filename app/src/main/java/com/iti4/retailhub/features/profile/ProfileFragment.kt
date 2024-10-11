package com.iti4.retailhub.features.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentProfileBinding
import com.iti4.retailhub.logic.ToolbarSetup
import com.iti4.retailhub.models.CountryCodes
import com.iti4.retailhub.models.CurrencySpinnerItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var isExpanded: Boolean = false;
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
        ToolbarSetup.setupToolbar(
            binding.profileAppbar,
            "My Profile",
            resources,
            findNavController()
        )
        binding.profileAppbar.collapsedPageName.visibility = View.GONE
        binding.profileOrderBtn.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_profileFragment_to_ordersFragment)
        }
        binding.profileSettingsBtn.setOnClickListener {
            if (!isExpanded) {
                binding.expandableLayout.visibility = View.VISIBLE
            } else {
                binding.expandableLayout.visibility = View.GONE
            }
            isExpanded = !isExpanded
        }
        binding.profileShippingBtn.setOnClickListener{
            val bundle = Bundle().apply {
                putString("reason", "profile")
            }
            requireActivity().findNavController(R.id.fragmentContainerView2)
                .navigate(R.id.addressFragment, bundle)
        }
    }

    private fun setupSpinner() {

        val options = listOf(
            CurrencySpinnerItem(CountryCodes.EGP),
            CurrencySpinnerItem(CountryCodes.USD),
            CurrencySpinnerItem(CountryCodes.EUR),
            CurrencySpinnerItem(CountryCodes.AED),
        )
        val adapter = CurrencySpinnerAdapter(requireActivity(), options)
        binding.spinnerCurrency.adapter = adapter
        binding.spinnerCurrency.post {
            binding.spinnerCurrency.setSelection(
                when (viewModel.getCurrencyCode()) {
                    CountryCodes.EGP -> 0
                    CountryCodes.USD -> 1
                    CountryCodes.EUR -> 2
                    CountryCodes.AED -> 3
                }
            )
        }
        binding.spinnerCurrency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedOption = options[position]
                    viewModel.setCurrencyCode(selectedOption.country)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

}