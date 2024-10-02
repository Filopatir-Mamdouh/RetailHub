package com.iti4.retailhub.loginandsignup.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    lateinit var signUpBinding: FragmentSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
    }


}