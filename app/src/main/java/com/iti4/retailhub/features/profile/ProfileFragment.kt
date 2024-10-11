package com.iti4.retailhub.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentProfileBinding
import com.iti4.retailhub.features.login_and_signup.viewmodel.UserAuthunticationViewModelViewModel
import com.iti4.retailhub.logic.ToolbarSetup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val authuntication: UserAuthunticationViewModelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         ToolbarSetup.setupToolbar(binding.profileAppbar,"My Profile", resources, findNavController())
         binding.profileAppbar.collapsedPageName.visibility = View.GONE
         binding.profileOrderBtn.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_ordersFragment) }
         binding.profileSettingsBtn2.setOnClickListener {
             if (authuntication.isUserLoggedIn()) {
                 authuntication.signOut()
             }
         }
     }
}