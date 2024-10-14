package com.iti4.retailhub.features.shop.search

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.MainActivity
import com.iti4.retailhub.R
import com.iti4.retailhub.constants.SortBy
import com.iti4.retailhub.databinding.FragmentSearchBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.favorits.viewmodel.FavoritesViewModel
import com.iti4.retailhub.features.home.OnClickGoToDetails
import com.iti4.retailhub.features.productdetails.viewmodel.ProductDetailsViewModel
import com.iti4.retailhub.features.shop.search.adapter.GridViewAdapter
import com.iti4.retailhub.features.shop.search.adapter.ListViewAdapter
import com.iti4.retailhub.features.shop.search.sortby.SortByBottomSheetFragment
import com.iti4.retailhub.features.shop.search.sortby.SortByListener
import com.iti4.retailhub.features.shop.search.viewmodels.SearchViewModel
import com.iti4.retailhub.models.CountryCodes
import com.iti4.retailhub.models.HomeProducts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

@AndroidEntryPoint
class SearchFragment : Fragment(), OnClickGoToDetails, SortByListener {
    private val viewModel: SearchViewModel by viewModels()
    private val favoritesViewModel by viewModels<FavoritesViewModel>()
    private val productDetailsViewModel by viewModels<ProductDetailsViewModel>()
    private lateinit var currencyCode: CountryCodes
    private var conversionRate: Double = 0.0
    private lateinit var binding: FragmentSearchBinding
    private var isListView = true
    private lateinit var listViewAdapter: ListViewAdapter
    private lateinit var gridViewAdapter: GridViewAdapter
    private var filterQuery: String = ""
    private var query: String = ""
    private var typeQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesViewModel.getFavorites()
        currencyCode = viewModel.getCurrencyCode()
        conversionRate = viewModel.getConversionRates(currencyCode)
        listViewAdapter = ListViewAdapter(this, currencyCode, conversionRate)
        gridViewAdapter = GridViewAdapter(this, currencyCode, conversionRate)
        onSwitchViewClicked()
        filterQuery = arguments?.getString("query") ?: ""
        typeQuery = arguments?.getString("type") ?: ""
        binding.filterGroup.setOnClickListener { findNavController().navigate(R.id.filterFragment) }
        setupDataListener()
        setupChipGroup()
        if (filterQuery.isNotEmpty()){
            search()
        }
        setupAppbar()
        clearFilters()
    }

    private fun setupDataListener() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchList.combine(favoritesViewModel.savedFavortes){
                        products,favorites->
                    handleProductsAndFavoritesCombination(products,favorites) }.collect {
                    when (it) {
                        is ApiState.Success<*> -> {
                            handleDataResult(it.data as List<HomeProducts>)
                        }

                        is ApiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                it.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ApiState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun handleDataResult(data: List<HomeProducts>) {
        Log.d("Filo", "handleDataResult: ${data.size}")
        if (isListView) {
            binding.searchRV.apply {
                adapter = listViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                listViewAdapter.submitList(data)
            }
        } else {
            binding.searchRV.apply {
                adapter = gridViewAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                gridViewAdapter.submitList(data)
            }
        }
    }

    private fun onSwitchViewClicked() {
        binding.switchView.setOnClickListener {
            isListView = !isListView
            if (isListView) {
                binding.searchRV.apply {
                    adapter = listViewAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    listViewAdapter.submitList(gridViewAdapter.currentList)
                }
                binding.switchView.setImageResource(R.drawable.view_by_grid)
            } else {
                binding.searchRV.apply {
                    adapter = gridViewAdapter
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    gridViewAdapter.submitList(listViewAdapter.currentList)
                }
                binding.switchView.setImageResource(R.drawable.view_by_list)
            }
        }
    }

    override fun goToDetails(productId: String) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            bundleOf("productid" to productId)
        )
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
        Toast.makeText(requireContext(), "Added to your favorites", Toast.LENGTH_SHORT).show()
    }

    override fun deleteFromCustomerFavorites(pinFavorite: String) {
        favoritesViewModel.deleteFavorites(pinFavorite)
        Toast.makeText(requireContext(), "Deleted from Favorites", Toast.LENGTH_SHORT).show()
    }


    private fun search(){
        val finalQuery = StringBuilder().append(query).append(" $filterQuery").append(" AND $typeQuery").toString()
        Log.d("Filo", "search: $finalQuery")
        viewModel.searchProducts(finalQuery)
    }

    private fun setupAppbar() {
        binding.apply {
            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val totalScrollRange = appBarLayout.totalScrollRange
                if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                    // Collapsed
                    toolbar.background = ResourcesCompat.getDrawable(resources, R.color.white, null)
                    pageName.visibility = View.GONE
                    collapsedPageName.visibility = View.VISIBLE
                } else {
                    // Expanded
                    toolbar.background = ResourcesCompat.getDrawable(resources, R.color.white, null)
                    pageName.visibility = View.VISIBLE
                    collapsedPageName.visibility = View.GONE
                }
            }
            backButton.setOnClickListener { findNavController().navigateUp() }
            searchBtn.setOnClickListener {
                searchBtn.animate().translationX(0f).setDuration(400).withStartAction {
                    collapsedPageName.alpha = 0f
                    editTextText.animate().alpha(1f).translationX(-500f).setDuration(400)
                        .withEndAction {
                            editTextText.requestFocus()
                        }.start()
                }.start()
            }
            editTextText.setOnKeyListener { _, key, _ ->
                if (key == KeyEvent.KEYCODE_ENTER) {
                    query = if (editTextText.text.isEmpty()) "" else "title:${editTextText.text}"
                    search()
                    searchBtn.animate().translationX(0f).setDuration(400).withStartAction {
                        collapsedPageName.alpha = 1f
                        editTextText.animate().alpha(0f).translationX(0f).setDuration(400).start()
                        (activity as MainActivity).hideKeyboard()
                    }.start()
                    true
                } else false
            }
            sortGroup.setOnClickListener {
                SortByBottomSheetFragment(this@SearchFragment).show(childFragmentManager, "sort")
            }
        }
    }


    private fun setupChipGroup(){
        when (typeQuery.lowercase(Locale.ROOT)){
            "shoes" -> binding.Shoes.isChecked = true
            "t-shirts" -> binding.Tops.isChecked = true
            "accessories" -> binding.accsChip.isChecked = true
            else -> binding.allChip.isChecked = true
        }
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            typeQuery = ""
            if(checkedIds.isNotEmpty()) {
                typeQuery = view?.findViewById<Chip>(checkedIds[0])?.text.toString()
                checkedIds.removeFirst()
                checkedIds.map { id -> typeQuery += " OR " + view?.findViewById<Chip>(id)?.text.toString() }
            }
            if (typeQuery.contains("All") || typeQuery.isEmpty()) {
                typeQuery = ""
                binding.allChip.isChecked = true
            }
            search()
        }
    }

    private fun clearFilters() {
        binding.imageView8.setOnClickListener {
            binding.imageView8.visibility = View.GONE
            filterQuery = ""
            search()
        }
    }

    private fun handleProductsAndFavoritesCombination(products: ApiState, favorites: ApiState): ApiState{
        if (products is ApiState.Success<*> && favorites is ApiState.Success<*>){
            val productsList = products.data as List<HomeProducts>
            val data = favorites.data as GetCustomerFavoritesQuery.Customer
            val favoritesList = data.metafields.nodes.filter { it.key == "favorites" }
            val combinedList = productsList.map { product ->
                favoritesList.forEach {
                        node -> if(node.key == "favorites" && node.value == product.id){
                    product.favID = node.id
                    product.isFav = true
                }
                }
                return@map product
            }
            return ApiState.Success(combinedList)
        }
        else if (products is ApiState.Loading || favorites is ApiState.Loading){
            return ApiState.Loading
        }
        else
            return if (products is ApiState.Error) products else favorites
    }

    override fun onSortBySelected(sortBy: SortBy) {
        Log.d("Filo", "onSortBySelected: $sortBy")
        when(sortBy){
            SortBy.PRICE_ASC -> {
                binding.textView15.text = getString(R.string.price_low_to_high)
                if (isListView){
                    listViewAdapter.submitList(listViewAdapter.currentList.sortedBy { it.maxPrice.toDouble() })
                } else {
                    gridViewAdapter.submitList(gridViewAdapter.currentList.sortedBy { it.maxPrice.toDouble() })
                }
            }
            SortBy.PRICE_DESC -> {
                binding.textView15.text = getString(R.string.price_high_to_low)
                if (isListView){
                    listViewAdapter.submitList(listViewAdapter.currentList.sortedByDescending { it.maxPrice.toDouble() })
                } else {
                    gridViewAdapter.submitList(gridViewAdapter.currentList.sortedByDescending { it.maxPrice.toDouble() })
                }
            }
            SortBy.TITLE -> {
                binding.textView15.text = getString(R.string.name_a_to_z)
                if (isListView){
                    listViewAdapter.submitList(listViewAdapter.currentList.sortedBy { it.title?.split(" | ")?.get(1) })
                    Log.d("Filo", "onSortBySelected: ${listViewAdapter.currentList}")
                } else {
                    gridViewAdapter.submitList(gridViewAdapter.currentList.sortedBy { it.title?.split("|")?.get(1) })
                }
            }
        }
        listViewAdapter.notifyDataSetChanged()
        gridViewAdapter.notifyDataSetChanged()
    }
}