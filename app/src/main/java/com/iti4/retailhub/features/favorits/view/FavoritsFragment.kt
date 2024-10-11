package com.iti4.retailhub.features.favorits.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.FragmentFavoritsBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.favorits.view.adapter.FavoritsDiffUtilAdapter
import com.iti4.retailhub.features.favorits.view.adapter.OnFavoritItemClocked
import com.iti4.retailhub.features.favorits.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritsFragment : Fragment(), OnFavoritItemClocked {
    private val favoritesViewModel by viewModels<FavoritesViewModel>()
    lateinit var binding: FragmentFavoritsBinding

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


        val favoritesAdapter = FavoritsDiffUtilAdapter(requireContext(),this)
        binding.favoritsRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.favoritsRecycleView.adapter = favoritesAdapter



        favoritesViewModel.getFavorites()


        lifecycleScope.launch {
            favoritesViewModel.savedFavortes.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {

                        val data = item.data as GetCustomerFavoritesQuery.Customer


                        binding.favoritProgress.visibility = View.GONE
                        Log.d("fav", "onViewCreated:${data} ")


                        val favoritList=data.metafields.nodes
                        favoritesAdapter.submitList(favoritList.filter { it.key == "favorites" })


                        if(favoritList.isEmpty()){
                            binding.lottibagAnimation.visibility=View.VISIBLE
                        }else{
                            binding.lottibagAnimation.visibility=View.GONE
                        }
                    }

                    is ApiState.Error -> {
                        Toast.makeText(
                            requireContext(),
                           /* item.exception.message*/"Error can't delete product, try again",
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

    override fun showDeleteAlert(id: String) {
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.favorit_delete_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)

        btnYes.setOnClickListener {
            deleteFavoritProduct(id)
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun deleteFavoritProduct(id: String) {

        favoritesViewModel.deleteFavorites(id)

         lifecycleScope.launch {
            favoritesViewModel.deletedFavortes.collect { item ->

                when (item) {

                    is ApiState.Success<*> -> {
                        Toast.makeText(
                            requireContext(),
                           "Product Is Deleted Successfully",
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