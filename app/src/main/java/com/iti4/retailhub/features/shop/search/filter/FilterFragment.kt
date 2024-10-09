package com.iti4.retailhub.features.shop.search.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentFilterBinding
import com.iti4.retailhub.features.shop.viewmodels.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : Fragment() {
    private val viewModel by viewModels<FilterViewModel>()
    private lateinit var binding: FragmentFilterBinding
    private val query = mutableMapOf<String, String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyBtn.setOnClickListener {
            binding.apply {
                // ! TODO Later
//                chipGroup3.setOnCheckedStateChangeListener { _, checkedIds ->
//                    if (checkedIds.isNotEmpty())
//                        query["title"] = view.findViewById<Chip>(checkedIds[0]).text.toString()
//                }
            }
            findNavController().navigate(R.id.action_filterFragment_to_searchFragment, bundleOf("query" to query))
        }
    }
}