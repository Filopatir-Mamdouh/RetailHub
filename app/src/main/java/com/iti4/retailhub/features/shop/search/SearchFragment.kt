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
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentSearchBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.home.OnClickGoToDetails
import com.iti4.retailhub.features.shop.search.adapter.GridViewAdapter
import com.iti4.retailhub.features.shop.search.adapter.ListViewAdapter
import com.iti4.retailhub.features.shop.search.viewmodels.SearchViewModel
import com.iti4.retailhub.models.HomeProducts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

@AndroidEntryPoint
class SearchFragment : Fragment(), OnClickGoToDetails {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private var isListView = true
    private val listViewAdapter by lazy { ListViewAdapter(this) }
    private val gridViewAdapter by lazy { GridViewAdapter(this) }
    private var filterQuery : String = ""
    private var query : String = ""
    private var typeQuery : String = ""

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
        filterQuery = arguments?.getString("query") ?: ""
        typeQuery = arguments?.getString("type") ?: ""
        binding.filterGroup.setOnClickListener { findNavController().navigate(R.id.filterFragment) }
        onSwitchViewClicked()
        setupDataListener()
        setupChipGroup()
        if (filterQuery.isNotEmpty()){
            search()
        }
        setupAppbar()
        clearFilters()
    }

    private fun setupDataListener(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.searchList.collect{
                    when (it){
                        is ApiState.Success<*> -> {
                            handleDataResult(it.data as List<HomeProducts>)
                        }
                        is ApiState.Error -> {
                            Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT).show()
                        }
                        is ApiState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun handleDataResult(data: List<HomeProducts>){
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
        findNavController().navigate(R.id.productDetailsFragment, bundleOf("productid" to productId))
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
        //TODO("Not yet implemented")
    }

    private fun search(){
        val finalQuery = StringBuilder().append(filterQuery).append(" $query").append(" AND $typeQuery").toString()
        Log.d("Filo", "search: $finalQuery")
        viewModel.searchProducts(finalQuery)
    }

    private fun setupAppbar(){
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
                searchBtn.animate().translationX(-500f).setDuration(400).withStartAction {
                    collapsedPageName.alpha = 0f
                    editTextText.animate().alpha(1f).translationX(-500f).setDuration(400).withEndAction {
                        editTextText.requestFocus()
                    }.start()
                }.start()
            }
            editTextText.setOnKeyListener{
                    _,key,_ -> if (key == KeyEvent.KEYCODE_ENTER){
                query = "title: ${editTextText.text}"
                search()
                searchBtn.animate().translationX(0f).setDuration(400).withStartAction {
                    collapsedPageName.alpha = 1f
                    editTextText.animate().alpha(0f).setDuration(400).start()
                }.start()
                true
            }else false
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

    private fun clearFilters(){
        binding.imageView8.setOnClickListener {
            binding.imageView8.visibility = View.GONE
            filterQuery = ""
            search()
        }
    }
}