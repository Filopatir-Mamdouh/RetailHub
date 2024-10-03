package com.iti4.retailhub.features.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentHomeBinding
import com.iti4.retailhub.datastorage.network.ApiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()

    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED){
                viewModel.products.collect{ item ->
                    when(item){
                        is ApiState.Success<*> -> {
                            val data = item.data as ProductsQuery.Products
                            data.edges.forEach {
                                Log.d("Filo", "onViewCreated: ${it.node}")
                            }
                        }
                        is ApiState.Error -> {
                            Log.d("Filo", "onViewCreated: ${item.exception}")
                        }
                        is ApiState.Loading -> {
                            Log.d("Filo", "onViewCreated: Loading")
                        }
                    }
                }
            }
        }
    }
}