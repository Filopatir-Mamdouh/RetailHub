package com.iti4.retailhub.features.shop.search.filter.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iti4.retailhub.databinding.FragmentBrandsBottomSheetBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.shop.search.filter.bottomsheet.adapter.BrandsBottomSheetAdapter
import com.iti4.retailhub.models.Brands
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrandsBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentBrandsBottomSheetBinding

    private val viewModel: BrandsBottomSheetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrandsBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = BrandsBottomSheetAdapter()
        binding.bottomSheetBrandsRV.adapter = adapter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED){
                viewModel.brands.collect{
                    when(it){
                        is ApiState.Success<*> -> adapter.submitList(it.data as List<Brands>)
                        else -> {}
                    }
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()

        binding.root.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }
}