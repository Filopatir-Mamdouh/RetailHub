package com.iti4.retailhub.features.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentProfileBinding
import com.iti4.retailhub.features.login_and_signup.view.LoginAuthinticationActivity
import com.iti4.retailhub.features.login_and_signup.viewmodel.UserAuthunticationViewModelViewModel
import com.iti4.retailhub.logic.ToolbarSetup
import com.iti4.retailhub.models.CountryCodes
import com.iti4.retailhub.models.CurrencySpinnerItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var isExpanded: Boolean = false;
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val authuntication: UserAuthunticationViewModelViewModel by viewModels()

lateinit var intent:Intent

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
         intent = Intent(requireContext(), LoginAuthinticationActivity::class.java)
        binding.profileAppbar.collapsedPageName.visibility = View.GONE
        if (authuntication.isguestMode()) {
            binding.guestpp.visibility = View.VISIBLE
            binding.btnOkaypp.setOnClickListener {

                intent.putExtra("guest","guest")
                startActivity(intent)
                requireActivity().finish()
            }
        } else {
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
//--------------------------------------------------------
binding.profileLogoutBtn.setOnClickListener {
    showLoginOutAlert()
}
        binding.profileShippingBtn.setOnClickListener{
            val bundle = Bundle().apply {
                putString("reason", "profile")
            }
            requireActivity().findNavController(R.id.fragmentContainerView2)
                .navigate(R.id.addressFragment, bundle)
        }
    }}
    private fun showLoginOutAlert() {
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.favorit_delete_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)
        val tvmessage=dialog.findViewById<TextView>(R.id.tvMessage)
        tvmessage.text="Are you sure you want to loginout?"
        btnYes.setOnClickListener {
            viewModel.signOut()
            startActivity(intent)
            requireActivity().finish()
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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