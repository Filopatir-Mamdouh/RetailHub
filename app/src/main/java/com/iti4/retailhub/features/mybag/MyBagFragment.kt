package com.iti4.retailhub.features.mybag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.R
import com.iti4.retailhub.communicators.ToolbarController
import com.iti4.retailhub.databinding.FragmentMyBagBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CartProduct
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyBagFragment : Fragment(), OnClickMyBag {
    private lateinit var binding: FragmentMyBagBinding
    private val viewModel by viewModels<MyBagViewModel>()
    private val adapter by lazy {
        MyBagProductRecyclerViewAdapter(this)
    }
    private var totalPrice: Double? = null
    private var cartProductList: MutableList<CartProduct>? = null

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

                            val data = item.data as List<CartProduct>
                            if(!data.isNullOrEmpty()){
                                cartProductList = data.toMutableList()
                                updateTotalPrice()
                                adapter.submitList(data)
                                binding.lottibagAnimation.visibility=View.GONE
                            }
                            else
                                binding.lottibagAnimation.visibility=View.VISIBLE
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
        binding.btnCheckout.setOnClickListener {
            if(!cartProductList.isNullOrEmpty()){
                val bundle = Bundle().apply {
                    putParcelableArrayList("data",cartProductList as ArrayList<CartProduct>)
                    putDouble("totalprice", totalPrice ?: 0.0)
                }
                findNavController().navigate(R.id.checkoutFragment, bundle)
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

    override fun deleteMyBagItem(cartProduct: CartProduct) {
        viewModel.deleteMyBagItem(cartProduct.draftOrderId)
        cartProductList?.remove(cartProduct)
        updateTotalPrice()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.myBagProductsRemove.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            Toast.makeText(
                                this@MyBagFragment.requireContext(),
                                "Item Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
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


    override fun updateTotalPrice() {
        totalPrice = 0.0
        totalPrice = cartProductList?.sumOf {
            val price = it.itemPrice?.toDoubleOrNull() ?: 0.0
            price * it.itemQuantity

        }!!
        binding.tvMyBagProductPrice.text = "$totalPrice EGP"
    }

    private fun updateQuantity() {
        cartProductList?.forEach {
            if (it.didQuantityChanged) {
                viewModel.updateMyBagItem(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as ToolbarController).apply {
            setVisibility(true)
            setTitle("My Bag")
        }
    }


    override fun onStop() {
        updateQuantity()
        super.onStop()
    }
}


