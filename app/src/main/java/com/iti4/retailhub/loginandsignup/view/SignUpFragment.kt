package com.iti4.retailhub.loginandsignup.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentSignUpBinding
import com.iti4.retailhub.loginandsignup.model.UserAuthunticationViewModelViewModel
import com.iti4.retailhub.userauthuntication.AuthResultState
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    lateinit var signUpBinding: FragmentSignUpBinding
val userAuthViewModel: UserAuthunticationViewModelViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        signUpBinding = FragmentSignUpBinding.inflate(inflater, container, false)
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpBinding.redArrowGoToLogin.setOnClickListener {
            findNavController(view).navigate(R.id.action_signUpFragment_to_loginInFragment)
        }
        signUpBinding.sigInBtn.setOnClickListener {
            val name = signUpBinding.nameTextInput.editText?.text.toString()
            val email = signUpBinding.emailTextInput.editText?.text.toString()
            val password = signUpBinding.passowrdTex.editText?.text.toString()
            userAuthViewModel.createUser(email, password)
        }
        lifecycleScope.launch {
            userAuthViewModel.authStateFlow.collect { authResultState ->
                when (authResultState) {
                    is AuthResultState.Loading -> {
                        signUpBinding.progressBar.visibility = View.VISIBLE
                    }
                    is AuthResultState.Success -> {
                        signUpBinding.progressBar.visibility = View.GONE
                        val intent= Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    is AuthResultState.Failure -> {
                        signUpBinding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), authResultState.errorMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }


}