package com.iti4.retailhub.features.login_and_signup.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.databinding.FragmentLoginInBinding
import com.iti4.retailhub.features.login_and_signup.viewmodel.UserAuthunticationViewModelViewModel
import com.iti4.retailhub.userauthuntication.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginInFragment : Fragment() {

    val userAuthViewModel: UserAuthunticationViewModelViewModel by viewModels<UserAuthunticationViewModelViewModel>()
    lateinit var loginUpBinding: FragmentLoginInBinding
//    val userAuthViewModel: UserAuthunticationViewModelViewModel by viewModels<UserAuthunticationViewModelViewModel>()
    lateinit var customLoadingDialog : CustomLoadingDialog
    lateinit var customMesssageDialog : CustomMessageDialog
    val EMAIL_REGEX: String = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
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
        customLoadingDialog = CustomLoadingDialog(requireContext())
        customMesssageDialog = CustomMessageDialog(requireContext())


        loginUpBinding.sigInBtn.setOnClickListener {
            loginUpBinding.emailTextInput.error = null // Clear error message
            loginUpBinding.passowrdTex.error = null // Clear error message
            val email = loginUpBinding.emailTextInput.editText?.text.toString().removeSuffix(" ")
            val password = loginUpBinding.passowrdTex.editText?.text.toString().removeSuffix(" ")
            if (email.isEmpty()) {
                loginUpBinding.emailTextInput.error = "Please enter your email"
                return@setOnClickListener
            } else if (!email.matches(EMAIL_REGEX.toRegex())) {
                loginUpBinding.emailTextInput.error = "Invalid email format"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                loginUpBinding.passowrdTex.error = "Please enter your password"
                return@setOnClickListener
            } else if (password.length < 6) {
                loginUpBinding.passowrdTex.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            userAuthViewModel.signIn(email, password)
        }
        loginUpBinding.googleCard.setOnClickListener {
            Log.d("signingoog", "onViewCreated: googleCard")
            userAuthViewModel.signInWithGoogle()
        }
        loginUpBinding.guest.setOnClickListener {
            userAuthViewModel.setLoginStatus("guest")
            val intent= Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            userAuthViewModel.authState.collect { authResultState ->
                when (authResultState) {
                    is AuthState.Loading -> {
                        customLoadingDialog.show()
                    }
                    is AuthState.Success -> {
                        customLoadingDialog.dismiss()
                        userAuthViewModel.setLoginStatus("login")
                        val intent= Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    is AuthState.Messages -> {
                        if (authResultState.error!="Idle") {
                            customLoadingDialog.dismiss()
                            if (authResultState.error=="Email is not verified") {
                                customMesssageDialog.setText(authResultState.error)
                                customMesssageDialog.show()
                            }else if (authResultState.error=="Failed to send verification email") {
                                customMesssageDialog.setText(authResultState.error)
                                customMesssageDialog.show()
                            }else {
                                loginUpBinding.emailTextInput.error= authResultState.error
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
        Log.d("signingoog", " signInResultLauncher:")
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("signingoog", " signInResultLauncher:if1")
            if (result.data != null){
                Log.d("signingoog", " signInResultLauncher:if2")
                userAuthViewModel.handleSignInResult(result.data!!)
            }
        }
    }

}
