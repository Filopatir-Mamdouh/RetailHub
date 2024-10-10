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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.GetAddressesDefaultIdQuery
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.MainActivityViewModel
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentAddressBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CustomerAddress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment(), OnClickAddress {
    private val TAG: String = "AddressFragment"

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

    // Shared viewModels
    private val viewModel: AddressViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val adapter by lazy {
        // addressList is An Empty List initially
        AddressRecyclerViewAdapter(this, viewModel.addressesList)
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

        initButtonsClickListeners()

        // get list of saved addresses from graph
        viewModel.getAddressesById()
        // then calls getDefault address state
        listenToDefaultAddress()


    }


    private fun listenToDefaultAddress() {
        lifecycleScope.launch {
            viewModel.defaultAddressState.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val customer = item.data as GetAddressesDefaultIdQuery.Customer
                        val defaultAddress = customer.defaultAddress
                        if (defaultAddress != null) {
                            viewModel.addressesList.forEach {
                                if (it.id == defaultAddress.id)
                                    it.isDefault = true
                            }

//                            adapter.listData = (viewModel.addressesList)
//                            adapter.notifyDataSetChanged()
//
                            if (viewModel.addressesList.size > 0)
                                binding.addressNotFoundGroup.visibility = View.GONE
                            else
                                binding.addressNotFoundGroup.visibility = View.VISIBLE
                        }
                    }

                    is ApiState.Error -> {
                        Log.i("here", "listenToDefaultAddress On error")
                    }

                    is ApiState.Loading -> {
                        Log.i("here", "listenToDefaultAddress On loading")
                    }
                }
            }

        }
    }


    override fun onStart() {
        super.onStart()
//        viewModel.getAddressesById()
//        val reason = arguments?.getString("reason")
//        if (reason == "changeShipping") {
//            Log.i("here", "onStart:  change " + "here")
//        } else if (reason == "addNew") {
//            Log.i("here", "onStart:  add " + "here")
//        }
        (activity as MainActivity).hideBottomNavBar()
    }

    override fun onStop() {
        super.onStop()
        Log.i("here", "onStop: " + viewModel.addressesList.size)
        viewModel.updateMyAddresses(viewModel.addressesList)

        // viewModel.updateCustomerDefaultAddress()
        (activity as MainActivity).showBottomNavBar()
    }

    override fun onResume() {
        super.onResume()
        adapter.listData = (viewModel.addressesList)
        adapter.notifyDataSetChanged()

        if (viewModel.addressesList.size == 0)
            binding.addressNotFoundGroup.visibility = View.VISIBLE
        else
            binding.addressNotFoundGroup.visibility = View.GONE
    }


    override fun editDetails(address: CustomerAddress) {

        val bundle = Bundle().apply {
            putString("reason", "edit")
            putParcelable("data", address)
        }
        findNavController().navigate(R.id.addressDetailsFragment, bundle)
    }

    //callback when click on the delete icon
    override fun deleteAddress(id: String) {
        val index = viewModel.addressesList.indexOfFirst {
            it.id == id
        }
        viewModel.addressesList.remove(viewModel.addressesList[index])
        adapter.listData = (viewModel.addressesList)
        adapter.notifyDataSetChanged()
        if (viewModel.addressesList.size == 0)
            binding.addressNotFoundGroup.visibility = View.VISIBLE
        Log.i("here", "deleteAddress: " + viewModel.addressesList.size)
        viewModel.updateMyAddresses(viewModel.addressesList)
    }

    // callback when click on the checkbox
    override fun setDefaultAddress(position: Int) {
//        viewModel.updateCustomerDefaultAddress(address.id!!)
        var index = 0
        viewModel.addressesList.forEach {
            viewModel.addressesList[index].isDefault = false
            index += 1
        }
        viewModel.addressesList[position].isDefault = true
        viewModel.addressesList.forEach {
            Log.i("here", "each item: " + it.name + it.isDefault)
        }
        adapter.updateData(viewModel.addressesList)

//        viewModel.getDefaultAddress()
        // findNavController().navigateUp()
    }

    //callback when click on the cardView
    override fun checkoutClickedAnAddress(address: CustomerAddress) {
        mainActivityViewModel.customerChoseAnAddressNotDefault = true
        mainActivityViewModel.customerChosenAddress = address
        requireActivity().findNavController(R.id.fragmentContainerView2).navigateUp()
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

    private fun initButtonsClickListeners() {
        binding.rvAddress.apply {
            val manager = LinearLayoutManager(requireContext())
            manager.setOrientation(RecyclerView.VERTICAL)
            layoutManager = manager
            adapter = adapter
        }

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
    }
}