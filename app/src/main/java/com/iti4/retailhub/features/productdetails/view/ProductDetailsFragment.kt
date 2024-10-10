package com.iti4.retailhub.features.productdetails.view

import android.R
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathercast.alarmandnotification.view.ProductDetailsDiffUtilAdapter
import com.iti4.retailhub.CreateDraftOrderMutation
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.communicators.ToolbarController
import com.iti4.retailhub.databinding.FragmentProductDetailsBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.favorits.viewmodel.FavoritesViewModel
import com.iti4.retailhub.features.productdetails.viewmodel.ProductDetailsViewModel
import com.iti4.retailhub.features.reviwes.view.ReviewsDiffUtilAdapter
import com.iti4.retailhub.features.reviwes.viewmodel.ReviewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val favoritesViewModel by viewModels<FavoritesViewModel>()
    private val productDetailsViewModel by viewModels<ProductDetailsViewModel>()
    private val reviewsViewModel by viewModels<ReviewsViewModel>()


    lateinit var binding: FragmentProductDetailsBinding

    lateinit var produsctDetailsAdapter: ProductDetailsDiffUtilAdapter

    lateinit var favoritList: List<GetCustomerFavoritesQuery.Node>

    var productId = ""

    var idTodelete=""

    var addToFavoritsFirstClick=true


    var productVariants: List<ProductDetailsQuery.Edge>? = null
    var selectedProductVariantId: String = ""
    var selectedProductColor: String = ""
    var selectedProductSize: String = ""
    lateinit var allColors: List<String>
    lateinit var allSizes: List<String>
    lateinit var productTitle: String
    lateinit var seelectedImage: String
    var isVariantInCustomerDraftOrders = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getString("productid") as String
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sesMoreReviews.setOnClickListener {
            findNavController().navigate(com.iti4.retailhub.R.id.reviewsFragment)
        }


        produsctDetailsAdapter = ProductDetailsDiffUtilAdapter(requireContext())
        binding.productImages.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.productImages.adapter = produsctDetailsAdapter



        Log.d("fav", "onViewCreated: ${productId}")


        productDetailsViewModel.getProductDetails(productId)
        lifecycleScope.launch {
            productDetailsViewModel.productDetails.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        Log.d("fav", "onViewCreated:${item} ")
                        val data = item.data as ProductDetailsQuery.OnProduct

                        //get all variant that inventory>0
                        productVariants =
                            data.variants.edges.filter { it -> it.node.inventoryQuantity!! > 0 }

                        //set product details
                         productTitle = data.title
                        searchInCustomerFavorites()
                        searcheInBag()

                        val productTitleList = productTitle.split("|")
                        if (productTitleList.size > 2) {
                            binding.productPrand.text = productTitleList[2]
                        }
                        else {
                            binding.productPrand.text = productTitleList[1]
                        }
                        produsctDetailsAdapter.submitList(data.images.edges)
                        seelectedImage = data.images.edges[0].node.url.toString()
                        binding.textView14.text = productTitleList[0]
                        binding.productDescription.text = data.description
                        binding.productPrice.text =
                            "${productVariants!!.get(0).node.presentmentPrices.edges[0].node.price.amount} ${
                                productVariants!!.get(0).node.presentmentPrices.edges[0].node.price.currencyCode
                            }"
                        binding.textView14.text = productTitleList[1]
                        binding.inInventory.text =
                            "In Inventory: ${productVariants!!.get(0).node.inventoryQuantity}"



                        selectedProductVariantId = productVariants!!.get(0).node.id



                        //get all colors and sizes from variant
                         allSizes = productVariants!!
                            .mapNotNull { it.node.selectedOptions.find { option -> option.name == "Size" }?.value }
                            .distinct()
                         allColors = productVariants!!
                            .mapNotNull { it.node.selectedOptions.find { option -> option.name == "Color" }?.value }
                            .distinct()

                        val spinner2adapter =
                            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, allSizes)
                        spinner2adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        binding.spinner2.adapter = spinner2adapter

                        val images = intArrayOf(
                            R.drawable.ic_menu_view,
                            R.drawable.ic_menu_view,
                            R.drawable.ic_menu_view
                        )
                        val spinneradapter =
                            CustomSpinnerAdapter(requireContext(), allColors, images)
                        binding.spinner.adapter = spinneradapter

                        //get favorites
                        favoritesViewModel.getFavorites()

                    }
                    is ApiState.Error -> {
                        Toast.makeText(requireContext(), "Can't get product details, please reload page", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is ApiState.Loading -> {}
                }
            }
        }


                setupSpinners()

                getReviewes()

                binding.cardView3.setOnClickListener {
                    if(addToFavoritsFirstClick){
                        addToFavoritsFirstClick=false
                        addToFavorits()
                    }else{
                        addToFavoritsFirstClick=true
                        deleteFromFavorites()
                    }
                }

        }

    private fun deleteFromFavorites() {
        Log.d("deleteFromFavorites", "deleteFromFavorites:$idTodelete ")
        favoritesViewModel.deleteFavorites(idTodelete)
        lifecycleScope.launch {
            favoritesViewModel.deletedFavortes.collect { item ->

                when (item) {

                    is ApiState.Success<*> -> {
                        binding.imageView5oFavorits.setImageResource(com.iti4.retailhub.R.drawable.baseline_favorite_border_24)
                        Toast.makeText(
                            requireContext(),
                            "Product Is Deleted",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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

    private fun searchInCustomerFavorites() {
        productDetailsViewModel.searchProductInCustomerFavorites(selectedProductVariantId)
        lifecycleScope.launch {
            productDetailsViewModel.productInFavorites.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as GetCustomerFavoritesQuery.Customer
                        Log.d("searchInCustomerFavorites", "searchInCustomerFavorites:${selectedProductVariantId}${data} ")
                        if (data.metafields.nodes.isNotEmpty()) {
                            val node = data.metafields.nodes.find { it.namespace.contains(selectedProductVariantId) }

                            if(node?.namespace?.contains(selectedProductVariantId) == true) {
                                addToFavoritsFirstClick=false
                                binding.imageView5oFavorits.setImageResource(com.iti4.retailhub.R.drawable.fav_filled)
                                    idTodelete=node.id
                            }
                        }

                    }

                    is ApiState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Error, can't find in favorites please try again",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is ApiState.Loading -> {}
                }
            }
        }
    }

    private fun setupSpinners() {
        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    selectedProductColor = allColors[p2]
//                            Toast.makeText(requireContext(), p2.toString(), Toast.LENGTH_SHORT)
//                                .show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        binding.spinner2.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    selectedProductSize = allSizes[position]
                    selectedProductVariantId = productVariants!![position].node.id
                    binding.productPrice.text =
                        "${productVariants!![position].node.presentmentPrices.edges[0].node.price.amount} ${productVariants!![position].node.presentmentPrices.edges[0].node.price.currencyCode}"
                    binding.inInventory.text =
                        "In Inventory: ${productVariants!![position].node.inventoryQuantity}"
                    searcheInBag()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do something when no item is selected
                }
            }
    }

    private fun getReviewes() {
        val allReviews = reviewsViewModel.getAllReviews(3)
        val reviewsAdapter = ReviewsDiffUtilAdapter(requireContext())
        binding.reviewsRecycleView.layoutManager = LinearLayoutManager(context)
        binding.reviewsRecycleView.adapter = reviewsAdapter
        reviewsAdapter.submitList(allReviews)
    }

    private fun searcheInBag() {
        productDetailsViewModel.GetDraftOrdersByCustomer(productTitle)
        lifecycleScope.launch {
            productDetailsViewModel.customerDraftOrders.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as GetDraftOrdersByCustomerQuery.DraftOrders

                        Log.d("searcheInBag", "onViewCreated:${data} ")

                        if(data.nodes.isNotEmpty()){
                            val productVariantInBag= data.nodes[0].lineItems.nodes[0].variant?.id

                            if(!productVariantInBag.isNullOrEmpty()){
                                Log.d("searcheInBag", "searcheInBag:${productVariantInBag} ")
                                isVariantInCustomerDraftOrders=true
                                binding.addtocard.text = "Open In Your Bag"
                                addToBagButtonClickListner(true)
                            }
                        }else{
                            binding.addtocard.text = "Add To Bag"
                            addToBagButtonClickListner(false)
                        }
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


    private fun addToFavorits() {
        productDetailsViewModel.saveToFavorites(
            selectedProductVariantId,productId,
            selectedProductColor,selectedProductSize,
            productTitle.toString(),seelectedImage,binding.productPrice.text.toString()
        )
        lifecycleScope.launch {
            favoritesViewModel.savedFavortes.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as GetCustomerFavoritesQuery.Customer
                        Toast.makeText(
                            requireContext(),
                           "Add to your favorites",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.imageView5oFavorits.setImageResource( com.iti4.retailhub.R.drawable.fav_filled)
                        Log.d("fav", "onViewCreated:${data} ")

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

    private fun addToBagButtonClickListner(isAdd:Boolean){
        if(isAdd){
            binding.addtocard.setOnClickListener {
                findNavController().navigate(com.iti4.retailhub.R.id.myBagFragment)
            }
        }else{
            binding.addtocard.setOnClickListener {
                addToBag()
            }
        }
    }

    private fun addToBag(){
        Log.d("addToFavorits", "addToBag:${selectedProductVariantId} ")
        productDetailsViewModel.addToCart(selectedProductVariantId)
            lifecycleScope.launch {
                productDetailsViewModel.createDraftOrder.collect { item ->
                    when (item) {
                        is ApiState.Success<*> -> {
                            val data = item.data as CreateDraftOrderMutation.DraftOrderCreate
                            binding.addtocard.text = "Open In Your Bag"
                        }
                        is ApiState.Error -> {
                            Toast.makeText(requireContext(),"Can't add to bag, try again", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is ApiState.Loading -> {}
                    }
                }
            }
    }
    override fun onStart() {
        super.onStart()
        (requireActivity() as ToolbarController).apply {
            setVisibility(false)
            setTitle("")
        }
    }


}


//                binding.back.setOnClickListener {
//                    activity?.supportFragmentManager?.popBackStack()
//                }