package com.iti4.retailhub.features.address

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
import com.iti4.retailhub.GetAddressesByIdQuery
import com.iti4.retailhub.databinding.FragmentAddressBinding
import com.iti4.retailhub.datastorage.network.ApiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()

    private val adapter by lazy {
        AddressRecyclerViewAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = LinearLayoutManager(requireContext())
        manager.setOrientation(RecyclerView.VERTICAL)
        binding.rvAddress.layoutManager = manager
        binding.rvAddress.adapter = adapter
        adapter.submitList(
            listOf(
            )
        )
        listenToAddressesState()

    }


    private fun listenToAddressesState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.addressState.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            val response = item.data as  GetAddressesByIdQuery.Customer

                            Log.i("here", "listenToAddressesState: "+response)
                           adapter.submitList(response.addresses)

                        }

                        is ApiState.Error -> {
                            Log.i("here", "error: ")
                        }
                        is ApiState.Loading -> {}
                    }
                }
            }
        }

    }

}