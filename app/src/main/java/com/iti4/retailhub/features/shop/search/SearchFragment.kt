package com.iti4.retailhub.features.shop.search

import android.os.Bundle
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
import com.iti4.retailhub.features.shop.adapter.GridViewAdapter
import com.iti4.retailhub.features.shop.adapter.ListViewAdapter
import com.iti4.retailhub.features.shop.viewmodels.SearchViewModel
import com.iti4.retailhub.models.HomeProducts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), OnClickGoToDetails {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private var currentList = emptyList<HomeProducts>()
    private var isListView = true
    private val listViewAdapter by lazy { ListViewAdapter(this) }
    private val gridViewAdapter by lazy { GridViewAdapter(this) }
    private val query = mutableMapOf<String, String>()

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

        setupDataListener()

        onSwitchViewClicked()
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
            currentList = data
            listViewAdapter.submitList(data)
        } else {
            currentList = data
            gridViewAdapter.submitList(data)
        }
    }

    private fun onSwitchViewClicked() {
        binding.switchView.setOnClickListener {
            isListView = !isListView
            if (isListView) {
                binding.searchRV.apply {
                    adapter = listViewAdapter
                    layoutManager = LinearLayoutManager(requireContext())

                }
                binding.switchView.setImageResource(R.drawable.view_by_list)
                listViewAdapter.submitList(currentList)
            } else {
                binding.searchRV.apply {
                    adapter = listViewAdapter
                    layoutManager = GridLayoutManager(requireContext(), 2)
                }
                binding.switchView.setImageResource(R.drawable.view_by_grid)
                gridViewAdapter.submitList(currentList)
            }
        }
    }

    override fun goToDetails(productId: String) {
        findNavController().navigate(R.id.productDetailsFragment, bundleOf("productid" to productId))
    }
}