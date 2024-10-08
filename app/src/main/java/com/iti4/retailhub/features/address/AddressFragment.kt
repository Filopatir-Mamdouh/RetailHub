package com.iti4.retailhub.features.address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.GetAddressesByIdQuery
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentAddressBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CustomerAddress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment(), OnClickAddress {
    //animation
    private var animationClicked = false
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromButton: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.requireContext(),
            R.anim.from_button_anim
        )
    }
    private val toButton: Animation by lazy {
        AnimationUtils.loadAnimation(
            this.requireContext(),
            R.anim.to_button_anim
        )
    }
    private lateinit var binding: FragmentAddressBinding
    private val viewModel: AddressViewModel by activityViewModels()
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
        binding.btnAddAddress.setOnClickListener {
            startAnimation()
        }
        binding.btnAddAddressMap.setOnClickListener {
            findNavController().navigate(R.id.addressMapFragment)
        }
        binding.btnAddAddressText.setOnClickListener {
            val bundle = Bundle().apply {
                putString("reason", "new")
            }
            findNavController().navigate(R.id.addressDetailsFragment, bundle)
        }
        listenToAddressesStateFromServer()
        listenToAddressesEditState()
    }


    private fun listenToAddressesStateFromServer() {
        lifecycleScope.launch {

            viewModel.addressState.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        if (viewModel.runOnce) {
                            viewModel.runOnce = false
                            val response = item.data as GetAddressesByIdQuery.Customer
                            viewModel.addressesList =
                                queryCustomerAddressToCustomerAddress(response.addresses)
                            Log.i(
                                "here",
                                "new data: " + viewModel.addressesList.size + " and " + viewModel.addressesList
                            )
                            adapter.submitList(viewModel.addressesList)
                            if (viewModel.addressesList.size > 0)
                                binding.addressNotFoundGroup.visibility = View.GONE
                            else
                                binding.addressNotFoundGroup.visibility = View.VISIBLE
                        }
                    }

                    is ApiState.Error -> {
                        Log.i("here", "error: ")
                    }

                    is ApiState.Loading -> {}
                }
            }

        }
    }

    private fun listenToAddressesEditState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.editAddressState.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            val updatedOrNewAddress = item.data as CustomerAddress
                            updateAddressList(updatedOrNewAddress)
                            Log.i(
                                "here",
                                "update address" + updatedOrNewAddress.newAddress + " " + updatedOrNewAddress.name
                            )
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
        (activity as MainActivity).hideBottomNavBar()
    }

    override fun onStop() {
        super.onStop()
        viewModel.updateMyAddresses(viewModel.addressesList)
        (activity as MainActivity).showBottomNavBar()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.addressesList.size == 0)
            binding.addressNotFoundGroup.visibility = View.VISIBLE
        else
            binding.addressNotFoundGroup.visibility = View.GONE
    }


    private fun updateAddressList(address: CustomerAddress) {
        if (address.newAddress) {
            viewModel.addressesList.add(address)
        } else {
            val index = viewModel.addressesList.indexOfFirst { it.id == address.id }
            viewModel.addressesList[index] = address
        }
        adapter.submitList(viewModel.addressesList)
        adapter.notifyDataSetChanged()
    }

    override fun editDetails(address: CustomerAddress) {
        val bundle = Bundle().apply {
            putString("reason", "edit")
            putParcelable("data", address)
        }
        findNavController().navigate(R.id.addressDetailsFragment, bundle)
    }

    override fun deleteAddress(id: String) {
        val index = viewModel.addressesList.indexOfFirst {
            it.id == id
        }
        viewModel.addressesList.remove(viewModel.addressesList[index])
        adapter.notifyDataSetChanged()
        adapter.submitList(viewModel.addressesList)
        if (viewModel.addressesList.size == 0)
            binding.addressNotFoundGroup.visibility = View.VISIBLE
    }

    private fun queryCustomerAddressToCustomerAddress(address: List<GetAddressesByIdQuery.Address>): MutableList<CustomerAddress> {
        return address.map {
            CustomerAddress(
                it.address1!!,
                it.address2 + "," +
                        it.city + "," +
                        it.country,
                it.phone!!,
                it.name!!,
                id = it.id
            )
        }.toMutableList()
    }

    private fun startAnimation() {
        if (!animationClicked) {
            binding.btnAddAddressMap.visibility = View.GONE
            binding.btnAddAddressText.visibility = View.GONE
            binding.btnAddAddress.startAnimation(rotateOpen)
            binding.btnAddAddressMap.startAnimation(fromButton)
            binding.btnAddAddressText.startAnimation(fromButton)
        } else {
            binding.btnAddAddressMap.visibility = View.VISIBLE
            binding.btnAddAddressText.visibility = View.VISIBLE
            binding.btnAddAddress.startAnimation(rotateClose)
            binding.btnAddAddressMap.startAnimation(toButton)
            binding.btnAddAddressText.startAnimation(toButton)
        }
        animationClicked = !animationClicked
    }
}