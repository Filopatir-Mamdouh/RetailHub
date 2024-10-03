package com.iti4.retailhub.features.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentHomeBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.home.adapter.NewItemAdapter
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
                            binding.loadingGIF.visibility = View.GONE
                            val data = item.data as ProductsQuery.Products
                            displayUIData(data)
                        }
                        is ApiState.Error -> {
                            Toast.makeText(requireContext(), item.exception.message, Toast.LENGTH_SHORT).show()
                        }
                        is ApiState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun displayUIData(data: ProductsQuery.Products){
        binding.newItemRow.apply {
            title.text = getString(R.string.new_item)
            subtitle.text = getString(R.string.you_ve_never_seen_it_before)
            val adapter = NewItemAdapter()
            recyclerView.adapter = adapter
            adapter.submitList(data.edges)
        }
    }
}