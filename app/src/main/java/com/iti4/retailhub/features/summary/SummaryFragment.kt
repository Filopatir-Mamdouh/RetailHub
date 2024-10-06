package com.iti4.retailhub.features.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.iti4.retailhub.R
import com.iti4.retailhub.communicators.ToolbarController
import com.iti4.retailhub.databinding.FragmentSummaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryFragment : Fragment() {
    private lateinit var binding: FragmentSummaryBinding
    private val viewModel by viewModels<SummaryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinueShopping.setOnClickListener {

            findNavController().navigate(R.id.homeFragment)
        }
    }



    override fun onStart() {
        super.onStart()
        (requireActivity() as ToolbarController).apply {
            setVisibility(false)
        }
    }

}
