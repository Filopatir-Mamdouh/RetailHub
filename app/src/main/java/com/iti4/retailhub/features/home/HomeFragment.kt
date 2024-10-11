package com.iti4.retailhub.features.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.UpdateCustomerFavoritesMetafieldsMutation
import com.iti4.retailhub.databinding.FragmentHomeBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.favorits.viewmodel.FavoritesViewModel
import com.iti4.retailhub.features.home.adapter.AdsViewPagerAdapter
import com.iti4.retailhub.features.home.adapter.BrandAdapter
import com.iti4.retailhub.features.home.adapter.DotsIndicatorDecoration
import com.iti4.retailhub.features.home.adapter.NewItemAdapter
import com.iti4.retailhub.features.productdetails.viewmodel.ProductDetailsViewModel
import com.iti4.retailhub.models.Brands
import com.iti4.retailhub.models.CountryCodes
import com.iti4.retailhub.models.Discount
import com.iti4.retailhub.models.HomeProducts
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnClickGoToDetails {

    private val viewModel by viewModels<HomeViewModel>()
    private val favoritesViewModel by viewModels<FavoritesViewModel>()
    private val productDetailsViewModel by viewModels<ProductDetailsViewModel>()
    private lateinit var currencyCode: CountryCodes
    private var conversionRate: Double = 0.0
    private lateinit var dotsIndicator: DotsIndicator
    private var currentPosition = 0
    private var autoScrollJob: Job? = null // Job for the coroutine

    private lateinit var adsAdapter: AdsViewPagerAdapter
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
        currencyCode = viewModel.getCurrencyCode()
        conversionRate = viewModel.getConversionRates(currencyCode)
        displayAds()
        viewModel.getFavorites()


        lifecycleScope.launch {
            viewModel.savedFavortes.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as GetCustomerFavoritesQuery.Customer
                        val favoritList = data.metafields.nodes.filter { it.key == "favorites" }
                        getHomeProducts(favoritList)
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

        lifecycleScope.launch {
            viewModel.couponsState.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as List<Discount>
                        adsAdapter.setData(data)

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

    private fun getHomeProducts(favoritList: List<GetCustomerFavoritesQuery.Node>) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.products.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            binding.animationView.visibility = View.GONE
                            val data = item.data as List<HomeProducts>
                            displayNewItemRowData(data, favoritList)
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
        data: List<HomeProducts>,
        favoritList: List<GetCustomerFavoritesQuery.Node>
    ) {
        binding.newItemRow.apply {
            title.text = getString(R.string.new_item)
            subtitle.text = getString(R.string.you_ve_never_seen_it_before)
            val adapter =
                NewItemAdapter(this@HomeFragment, favoritList, currencyCode, conversionRate)
            recyclerView.adapter = adapter
            adapter.submitList(data)
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


    private fun displayAds() {
        val manager = LinearLayoutManager(this.requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.vpHomeAds.layoutManager = manager
        adsAdapter = AdsViewPagerAdapter(listOf())
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.vpHomeAds)
        binding.vpHomeAds.adapter = adsAdapter
        binding.vpHomeAds.addItemDecoration(
            DotsIndicatorDecoration(
                colorInactive = ContextCompat.getColor(this.requireContext(), R.color.red_color),
                colorActive = ContextCompat.getColor(this.requireContext(), R.color.black_variant)
            )
        )
        startAutoScroll()
    }


    private fun startAutoScroll() {
        autoScrollJob = lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                delay(3000)
                currentPosition++
                if (currentPosition == adsAdapter.itemCount) {
                    currentPosition = 0
                }
                binding.vpHomeAds.smoothScrollToPosition(currentPosition)
            }
        }
    }


    override fun goToDetails(productId: String) {
        val bundle = Bundle()
        bundle.putString("productid", productId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
    }

    override fun saveToFavorites(
        variantID: String,
        productId: String,
        selectedProductColor: String,
        selectedProductSize: String,
        productTitle: String,
        selectedImage: String,
        price: String
    ) {
        productDetailsViewModel.saveToFavorites(
            productId, productId,
            selectedProductColor, selectedProductSize,
            productTitle, selectedImage, price
        )
        lifecycleScope.launch {
            productDetailsViewModel.saveProductToFavortes.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data =
                            item.data as UpdateCustomerFavoritesMetafieldsMutation.CustomerUpdate
                        Toast.makeText(
                            requireContext(),
                            "Add to your favorites",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.d("fav", "onViewCreated:${data} ")
                        viewModel.getFavorites()
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

    override fun onDestroy() {
        super.onDestroy()
        autoScrollJob?.cancel()
    }
}