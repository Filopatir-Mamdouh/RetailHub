package com.iti4.retailhub.features.productdetails.view

import android.R
import android.os.Bundle
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
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.communicators.ToolbarController
import com.iti4.retailhub.databinding.FragmentProductDetailsBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.productdetails.viewmodel.ProductDetailsViewModel
import com.iti4.retailhub.features.reviwes.view.ReviewsDiffUtilAdapter
import com.iti4.retailhub.features.reviwes.viewmodel.ReviewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    lateinit var binding: FragmentProductDetailsBinding
    private val productDetailsViewModel by viewModels<ProductDetailsViewModel>()
    private val reviewsViewModel by viewModels<ReviewsViewModel>()
    lateinit var produsctDetailsAdapter: ProductDetailsDiffUtilAdapter
    var productId = ""
    var productVariants: List<ProductDetailsQuery.Edge>? = null
    var selectedProductVariantId: String = ""
    var isVariantInCustomerDraftOrders = false


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
        productId = arguments?.getString("productid") as String
        binding.sesMoreReviews.setOnClickListener {
            findNavController().navigate(com.iti4.retailhub.R.id.reviewsFragment)
        }
        produsctDetailsAdapter = ProductDetailsDiffUtilAdapter(requireContext())
        binding.productImages.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.productImages.adapter = produsctDetailsAdapter
        productDetailsViewModel.getProductDetails(productId)
        lifecycleScope.launch {
            productDetailsViewModel.productDetails.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as ProductDetailsQuery.OnProduct
                        productVariants =
                            data.variants.edges.filter { it -> it.node.inventoryQuantity!! > 0 }


                        val productTitle = data.title.split("|")
                        if (productTitle.size > 2)
                            (requireActivity() as ToolbarController).apply {
                                setTitle(productTitle[2])
                            }
                        else
                            (requireActivity() as ToolbarController).apply {
                                setTitle(productTitle[1])
                            }
                        produsctDetailsAdapter.submitList(data.images.edges)
                        binding.productPrand.text = productTitle[0]
                        binding.productDescription.text = data.description
                        binding.productPrice.text =
                            "${productVariants!!.get(0).node.presentmentPrices.edges[0].node.price.amount} ${
                                productVariants!!.get(0).node.presentmentPrices.edges[0].node.price.currencyCode
                            }"
                        binding.textView14.text = productTitle[1]
                        binding.inInventory.text =
                            "In Inventory: ${productVariants!!.get(0).node.inventoryQuantity}"
                        selectedProductVariantId = productVariants!!.get(0).node.id

                        val allSizes = productVariants!!
                            .mapNotNull { it.node.selectedOptions.find { option -> option.name == "Size" }?.value }
                            .distinct()
                        val allColors = productVariants!!
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


                    }

                    is ApiState.Error -> {
                        Toast.makeText(requireContext(), item.exception.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ApiState.Loading -> {

                    }
                }
            }
        }
        lifecycleScope.launch {
            productDetailsViewModel.createDraftOrder.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        val data = item.data as CreateDraftOrderMutation.DraftOrderCreate
                        Toast.makeText(
                            requireContext(),
                            "Added to your card",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ApiState.Error -> {
                        Toast.makeText(requireContext(), item.exception.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ApiState.Loading -> {

                    }
                }

                lifecycleScope.launch {
                    productDetailsViewModel.customerDraftOrders.collect { item ->
                        when (item) {
                            is ApiState.Success<*> -> {
                                val data = item.data as GetDraftOrdersByCustomerQuery.DraftOrders

                                // Iterate through draft orders
                                data.nodes.forEach { draftOrder ->
                                    // Iterate through line items in each draft order
                                    draftOrder.lineItems.nodes.forEach { lineItem ->
                                        // Check if the current line item has the variant ID you are looking for
                                        if (lineItem.variant?.id == selectedProductVariantId) {
                                            // Variant found, do something
                                            isVariantInCustomerDraftOrders = true
                                        }
                                    }
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

//                binding.back.setOnClickListener {
//                    activity?.supportFragmentManager?.popBackStack()
//                }


                binding.spinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            Toast.makeText(requireContext(), p2.toString(), Toast.LENGTH_SHORT)
                                .show()
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
                            selectedProductVariantId = productVariants!![position].node.id
                            binding.productPrice.text =
                                "${productVariants!![position].node.presentmentPrices.edges[0].node.price.amount} ${productVariants!![position].node.presentmentPrices.edges[0].node.price.currencyCode}"
                            binding.inInventory.text =
                                "In Inventory: ${productVariants!![position].node.inventoryQuantity}"
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Do something when no item is selected
                        }
                    }
                val allReviews = reviewsViewModel.getAllReviews(3)
                val reviewsAdapter = ReviewsDiffUtilAdapter(requireContext())
                binding.reviewsRecycleView.layoutManager = LinearLayoutManager(context)
                binding.reviewsRecycleView.adapter = reviewsAdapter
                reviewsAdapter.submitList(allReviews)

                binding.addtocard.setOnClickListener {
                    if (!isVariantInCustomerDraftOrders) {
                        binding.addtocard.text = "Item Added"
                        productDetailsViewModel.addToCart(selectedProductVariantId)
                        it.isEnabled=false
                    } else {
                        it.isEnabled=false
                        binding.addtocard.text = "Open In Your Cart"
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as ToolbarController).apply {
            setVisibility(true)
            setTitle("")
        }
    }


}

