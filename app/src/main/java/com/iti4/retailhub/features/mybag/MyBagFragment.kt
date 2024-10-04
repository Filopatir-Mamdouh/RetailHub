package com.iti4.retailhub.features.mybag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.databinding.FragmentMyBagBinding
import com.iti4.retailhub.datastorage.network.ApiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyBagFragment : Fragment() {
    private lateinit var binding: FragmentMyBagBinding
    private val viewModel by viewModels<MyBagViewModel>()
    private val adapter by lazy {
        MyBagProductRecyclerViewAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.products.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            val data = item.data as GetDraftOrdersByCustomerQuery.DraftOrders
                            viewModel.updateTotalPrice(data.nodes[0].lineItems.nodes)
                            adapter.submitList(data.nodes[0].lineItems.nodes)
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


        val manager = LinearLayoutManager(requireContext())
        manager.setOrientation(RecyclerView.VERTICAL)
        binding.rvMyBag.layoutManager = manager
        binding.rvMyBag.adapter = adapter
        adapter.submitList(
            listOf(
            )
        )
    }


}


