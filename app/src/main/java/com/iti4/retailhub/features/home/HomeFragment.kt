package com.iti4.retailhub.features.home

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
import androidx.recyclerview.widget.GridLayoutManager
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.UpdateCustomerFavoritesMetafieldsMutation
import com.iti4.retailhub.databinding.FragmentHomeBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.favorits.viewmodel.FavoritesViewModel
import com.iti4.retailhub.features.home.adapter.BrandAdapter
import com.iti4.retailhub.features.home.adapter.NewItemAdapter
import com.iti4.retailhub.features.productdetails.viewmodel.ProductDetailsViewModel
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.HomeProducts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnClickGoToDetails {

    private val viewModel by viewModels<HomeViewModel>()
    private val favoritesViewModel by viewModels<FavoritesViewModel>()
    private val productDetailsViewModel by viewModels<ProductDetailsViewModel>()
lateinit var adapter: NewItemAdapter
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NewItemAdapter(this@HomeFragment, emptyList())

        getHomeProducts()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.brands.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            val data = item.data as List<Brands>
                            displayBrandsRowData(data)
                        }

                        is ApiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                item.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ApiState.Loading -> {}
                    }
                }
            }
        }

    }
private fun getFavorites(){
    favoritesViewModel.getFavorites()
    lifecycleScope.launch {
        favoritesViewModel.savedFavortes.collect { item ->
            when (item) {
                is ApiState.Success<*> -> {
                    val data = item.data as GetCustomerFavoritesQuery.Customer
                    val favoritList = data.metafields.nodes.filter { it.key == "favorites" }

                    adapter.updateFavorites(favoritList)

                }
                is ApiState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        item.exception.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                is ApiState.Loading -> {}
            }
        }
    }
}
    private fun getHomeProducts() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.products.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            binding.animationView.visibility = View.GONE
                            val data = item.data as List<HomeProducts>
                            displayNewItemRowData(data)
                        }
                        is ApiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                item.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ApiState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun displayNewItemRowData(
        data: List<HomeProducts>
    ) {
        binding.newItemRow.apply {
            title.text = getString(R.string.new_item)
            subtitle.text = getString(R.string.you_ve_never_seen_it_before)

            recyclerView.adapter = adapter
            adapter.submitList(data)
            getFavorites()
        }
    }

    private fun displayBrandsRowData(data: List<Brands>) {
        binding.brandItemRow.apply {
            title.text = getString(R.string.brands)
            subtitle.text = getString(R.string.brands_subtitle)
            val adapter = BrandAdapter()
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapter
            adapter.submitList(data)
        }
    }

    override fun goToDetails(productId: String) {
        val bundle = Bundle()
        bundle.putString("productid", productId)
        findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment2, bundle)
    }

    override fun saveToFavorites(
        productId: String,
        productTitle: String,
        selectedImage: String,
        price: String
    ) {
        productDetailsViewModel.saveToFavorites(
            productId,productId,
            productTitle,selectedImage,price
        )
        lifecycleScope.launch {
            productDetailsViewModel.saveProductToFavortes.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as  UpdateCustomerFavoritesMetafieldsMutation.CustomerUpdate
                        Toast.makeText(
                            requireContext(),
                            "Add to your favorites",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.d("fav", "onViewCreated:${data} ")
                        favoritesViewModel.getFavorites()
                    }
                    is ApiState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            item.exception.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    is ApiState.Loading -> {}
                }
            }
        }
    }

    override fun deleteFromCustomerFavorites(pinFavorite: String) {
        favoritesViewModel.deleteFavorites(pinFavorite)
        lifecycleScope.launch {
            favoritesViewModel.deletedFavortes.collect { item ->

                when (item) {

                    is ApiState.Success<*> -> {
                        Toast.makeText(
                            requireContext(),
                            "Product Is Deleted",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        favoritesViewModel.getFavorites()
                    }

                    is ApiState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            item.exception.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is ApiState.Loading -> {}
                }
            }
        }
    }
}