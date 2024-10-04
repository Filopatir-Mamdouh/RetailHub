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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var oldList: MutableList<CartProduct>? = null

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
                            oldList = data.toMutableList()
                            updateTotalPrice()
                            adapter.submitList(data)
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
            Log.i("here", "onViewCreated: " + oldList?.get(1)!!.itemQuantity)
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

    override fun deleteMyBagItem(itemId: String) {
        viewModel.deleteMyBagItem(itemId)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.myBagProductsRemove.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            // viewModel.updateTotalPrice(data.nodes[0].lineItems.nodes)
                            Toast.makeText(
                                this@MyBagFragment.requireContext(),
                                "Item Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            //  adapter.submitList(data)
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
        var totalPrice = 0.0
        oldList!!.forEach{
            Log.i("here", "updateTotalPrice: " +it.itemQuantity+" q price " + it.itemPrice +" inv" + it.inventoryQuantity)
        }
        totalPrice = oldList?.sumOf {
            val price = it.itemPrice?.toDoubleOrNull() ?: 0.0
            price * it.itemQuantity

        }!!
        binding.tvMyBagProductPrice.text = "$totalPrice $"
    }
}


