package com.iti4.retailhub.loginandsignup.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentSignUpBinding
import com.iti4.retailhub.loginandsignup.viewmodel.UserAuthunticationViewModelViewModel
import com.iti4.retailhub.userauthuntication.AuthState
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    lateinit var signUpBinding: FragmentSignUpBinding
    val userAuthViewModel: UserAuthunticationViewModelViewModel by viewModels<UserAuthunticationViewModelViewModel>()
    lateinit var customMesssageDialog :CustomMessageDialog
    lateinit var customLoadingDialog :CustomLoadingDialog
    lateinit var userName:String
    val EMAIL_REGEX: String = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
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
        customLoadingDialog = CustomLoadingDialog(requireContext())
        customMesssageDialog = CustomMessageDialog(requireContext())
        signUpBinding.redArrowGoToLogin.setOnClickListener {
            findNavController(view).navigate(R.id.action_signUpFragment_to_loginInFragment)
        }
        signUpBinding.sigInBtn.setOnClickListener {
            signUpBinding.nameTextInput.error = null // Clear error message
            signUpBinding.emailTextInput.error = null // Clear error message
            signUpBinding.passowrdTex.error = null // Clear error message
            userName = signUpBinding.nameTextInput.editText?.text.toString()
            val email = signUpBinding.emailTextInput.editText?.text.toString()
            val password = signUpBinding.passowrdTex.editText?.text.toString()
            if (userName.isEmpty()) {
                signUpBinding.nameTextInput.error = "Please enter your name"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                signUpBinding.emailTextInput.error = "Please enter your email"
                return@setOnClickListener
            } else if (!email.matches(EMAIL_REGEX.toRegex())) {
                signUpBinding.emailTextInput.error = "Invalid email format"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                signUpBinding.passowrdTex.error = "Please enter your password"
                return@setOnClickListener
            } else if (password.length < 6) {
                signUpBinding.passowrdTex.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            userAuthViewModel.createUser(userName, email, password)
        }
        signUpBinding.googleCard.setOnClickListener {
            userAuthViewModel.signInWithGoogle()
        }
        lifecycleScope.launch {
            userAuthViewModel.authState.collect { authResultState ->
                when (authResultState) {
                    is AuthState.Loading -> {
                        customLoadingDialog.show()
                    }
                    is AuthState.Success -> {
                        customLoadingDialog.dismiss()
                        val intent= Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    is AuthState.Messages -> {
                        if (authResultState.error!="Idle") {
                            customLoadingDialog.dismiss()
                            if (authResultState.error=="Verification email sent") {
                                customMesssageDialog.setText(authResultState.error)
                                customMesssageDialog.show()
                            }else if (authResultState.error=="Failed to send verification email") {
                                customMesssageDialog.setText(authResultState.error)
                                customMesssageDialog.show()
                            }else {
                                Toast.makeText(
                                    requireContext(),
                                    authResultState.error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    is AuthState.SignInIntent -> {
                        customLoadingDialog.dismiss()
                        val request = IntentSenderRequest.Builder(authResultState.intentSender).build()
                        signInResultLauncher.launch(request)
                    }
                }
            }
        }
    }
    private val signInResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null){
            userAuthViewModel.handleSignInResult(result.data!!)
                }
        }
    }

}