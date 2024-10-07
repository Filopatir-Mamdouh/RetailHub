package com.iti4.retailhub.features.favorits.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.UpdateCustomerFavoritesMetafieldsMutation
import com.iti4.retailhub.communicators.ToolbarController
import com.iti4.retailhub.databinding.FragmentFavoritsBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.favorits.view.adapter.FavoritsDiffUtilAdapter
import com.iti4.retailhub.features.favorits.view.adapter.OnFavoritItemClocked
import com.iti4.retailhub.features.favorits.viewmodel.FavoritesViewModel
import com.iti4.retailhub.features.productdetails.viewmodel.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritsFragment : Fragment(), OnFavoritItemClocked {
    private val favoritesViewModel by viewModels<FavoritesViewModel>()
    lateinit var binding: FragmentFavoritsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as ToolbarController).apply {
            setTitle("Favorites")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoritsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoritsRecycleView
        val favoritesAdapter = FavoritsDiffUtilAdapter(requireContext(),this)
        binding.favoritsRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.favoritsRecycleView.adapter = favoritesAdapter
        favoritesViewModel.getFavorites()
        lifecycleScope.launch {
            favoritesViewModel.savedFavortes.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as GetCustomerFavoritesQuery.Customer

                        Log.d("fav", "onViewCreated:${data} ")
                        favoritesAdapter.submitList(data.metafields.nodes.filter { it.key == "favorites" })
                    }

                    is ApiState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            item.exception.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is ApiState.Loading -> {

                    }
                }
            }
        }

    }


    override fun onShowFavoritItemDetails(variantId: String) {
        val bundle = Bundle()
        bundle.putString("productid", variantId)
        findNavController().navigate(R.id.productDetailsFragment, bundle)
    }

}