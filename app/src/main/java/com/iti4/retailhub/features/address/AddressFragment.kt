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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.iti4.retailhub.GetAddressesByIdQuery
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.R
import com.iti4.retailhub.communicators.ToolbarController
import com.iti4.retailhub.databinding.FragmentAddressBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CustomerAddress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment(), OnClickAddress {
    private lateinit var addressesList: List<GetAddressesByIdQuery.Address>
    private lateinit var binding: FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()

    private val adapter by lazy {
        AddressRecyclerViewAdapter(this)
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



        binding.btnAddAddress.setOnClickListener {

        }
        binding.btnAddAddressText.setOnClickListener {
            val bundle = Bundle().apply {
                putString("reason", "new")
            }
            findNavController().navigate(R.id.addressDetails, bundle)
        }
    }


    private fun listenToAddressesState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.addressState.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            val response = item.data as GetAddressesByIdQuery.Customer
                            addressesList = response.addresses
                            adapter.submitList(addressesList)
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

    override fun onStart() {
        super.onStart()
        (requireActivity() as ToolbarController).apply {
            setVisibility(true)
            setTitle("Addresses")
            collapse()
        }

        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.navigationView).visibility =
            View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.navigationView).visibility =
            View.VISIBLE
        (requireActivity() as ToolbarController).apply {
            expand()
        }

    }

    override fun saveAddressDetails(address: CustomerAddress) {
        //
    }

    override fun editDetails(address: CustomerAddress) {
        val bundle = Bundle().apply {
            putString("reason", "edit")
            putParcelable("data", address)
        }
        findNavController().navigate(R.id.addressDetails, bundle)

    }

}