package com.iti4.retailhub.loginandsignup.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentLoginInBinding
import com.iti4.retailhub.databinding.FragmentSignUpBinding
import com.iti4.retailhub.loginandsignup.model.UserAuthunticationViewModelViewModel
import com.iti4.retailhub.userauthuntication.AuthResultState
import kotlinx.coroutines.launch

class LoginInFragment : Fragment() {

    lateinit var loginUpBinding: FragmentLoginInBinding
    val userAuthViewModel: UserAuthunticationViewModelViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        loginUpBinding = FragmentLoginInBinding.inflate(inflater, container, false)
        return loginUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginUpBinding.sigInBtn.setOnClickListener {
            val email = loginUpBinding.emailTextInput.editText?.text.toString()
            val password = loginUpBinding.passowrdTex.editText?.text.toString()
            userAuthViewModel.createUser(email, password)
        }
        lifecycleScope.launch {
            userAuthViewModel.authStateFlow.collect { authResultState ->
                when (authResultState) {
                    is AuthResultState.Loading -> {
                        loginUpBinding.progressBar.visibility = View.VISIBLE
                    }
                    is AuthResultState.Success -> {
                        loginUpBinding.progressBar.visibility = View.GONE
                        val intent= Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    is AuthResultState.Failure -> {
                        loginUpBinding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), authResultState.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}