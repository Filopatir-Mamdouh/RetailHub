package com.iti4.retailhub.features.login_and_signup.view

import android.app.Activity
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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentLoginInBinding
import com.iti4.retailhub.features.login_and_signup.viewmodel.UserAuthunticationViewModel
import com.iti4.retailhub.userauthuntication.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginInFragment : Fragment() {

    val userAuthViewModel: UserAuthunticationViewModel by viewModels<UserAuthunticationViewModel>()
    lateinit var loginUpBinding: FragmentLoginInBinding
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

        loginUpBinding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        loginClickListner()


        loginUpBinding.googleCard.setOnClickListener {
//            userAuthViewModel.signInWithGoogle()
            googleSignIn()
        }

        loginUpBinding.guest.setOnClickListener {
            userAuthViewModel.setLoginStatus("guest")
            val intent= Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
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
                        requireActivity().finish()
                    }
                    is AuthState.Messages -> {
                        if (authResultState.error!="Idle") {
                            customLoadingDialog.dismiss()
                            if (authResultState.error=="Email is not verified") {
                                customMesssageDialog.setText(authResultState.error,"Verify your email and login again")
                                customMesssageDialog.show()
                            }else if (authResultState.error=="Failed to send verification email") {
                                customMesssageDialog.setText(authResultState.error,"Please try again")
                                customMesssageDialog.show()
                            }else {
                                Toast.makeText(
                                    requireContext(),
                                    "No internet connection",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                   /* is AuthState.SignInIntent -> {

                        customLoadingDialog.dismiss()
                        val request = IntentSenderRequest.Builder(authResultState.intentSender).build()
                      *//*  signInResultLauncher.launch(request)*//*
                    }*/
                    is AuthState.Error -> {}
                }
            }
        }
    }

    private fun loginClickListner(){
        loginUpBinding.sigInBtn.setOnClickListener {
            loginUpBinding.emailTextInput.isErrorEnabled  = false// Clear error message
            loginUpBinding.passowrdTex.isErrorEnabled  = false
            loginUpBinding.emailTextInput.error = null // Clear error message
            loginUpBinding.passowrdTex.error = null // Clear error message
            val email = loginUpBinding.emailTextInput.editText?.text.toString().removeSuffix(" ")
            val password = loginUpBinding.passowrdTex.editText?.text.toString().removeSuffix(" ")
            if (email.isEmpty()) {
                loginUpBinding.emailTextInput.isErrorEnabled  = true
                loginUpBinding.emailTextInput.error = "Please enter your email"
                return@setOnClickListener
            } else if (!email.matches(EMAIL_REGEX.toRegex())) {
                loginUpBinding.emailTextInput.isErrorEnabled  = true
                loginUpBinding.emailTextInput.error = "Invalid email format"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                loginUpBinding.passowrdTex.isErrorEnabled  = true
                loginUpBinding.passowrdTex.error = "Please enter your password"
                return@setOnClickListener
            } else if (password.length < 6) {
                loginUpBinding.passowrdTex.isErrorEnabled  = true
                loginUpBinding.passowrdTex.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            userAuthViewModel.signIn(email, password)
        }
    }
    /*private val signInResultLauncher = registerForActivityResult(
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
    }*/


    private fun showGuestDialog(){
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.guest_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val btnYes: Button = dialog.findViewById(R.id.btn_okayd)
        val btnNo: Button = dialog.findViewById(R.id.btn_canceld)
        val messag=dialog.findViewById<TextView>(R.id.messaged)
        messag.text="login to add to your favorites"
        btnYes.setOnClickListener {
            val intent = Intent(requireContext(), LoginAuthinticationActivity::class.java)
            intent.putExtra("guest","guest")
            startActivity(intent)
            requireActivity().finish()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



    //----------------------------------------------
    private fun googleSignIn(){
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(requireActivity(), options)
            val signInIntent = client.signInIntent
            startActivityForResult(signInIntent, 1234)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account?.idToken?.let { idToken ->
                        userAuthViewModel.signWithGoogle(idToken)
                    }
                } catch (e: ApiException) {
                    Log.e("LoginInFragment", "Google sign-in failed", e)
                }
            }
        }

}
