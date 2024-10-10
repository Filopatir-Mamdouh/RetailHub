package com.iti4.retailhub.features.shop.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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

@AndroidEntryPoint
class SearchFragment : Fragment(), OnClickGoToDetails {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private var isListView = true
    private val listViewAdapter by lazy { ListViewAdapter(this) }
    private val gridViewAdapter by lazy { GridViewAdapter(this) }
    private var filterQuery : String? = null
    private var query : String = ""

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
        filterQuery = arguments?.getString("query")
        binding.filterGroup.setOnClickListener { findNavController().navigate(R.id.filterFragment) }
        onSwitchViewClicked()
        setupDataListener()
        if (filterQuery != null){
            Log.d("Filo", "onViewCreated: $filterQuery")
            viewModel.searchProducts(filterQuery.toString())
        }
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
    private fun search(){
        if (this.filterQuery != null){
            this.filterQuery = StringBuilder().append(this.filterQuery).append(" ").append(query).toString()
        }
        else this.filterQuery = query
        viewModel.searchProducts(this.filterQuery.toString())
    }
}