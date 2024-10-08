package com.iti4.retailhub.features.shop.adapter

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.RvShopProductItemBinding
import com.iti4.retailhub.features.home.OnClickGoToDetails
import com.iti4.retailhub.features.home.adapter.HomeProductsDiffUtils
import com.iti4.retailhub.models.HomeProducts

class ListViewAdapter(val handleAction: OnClickGoToDetails): ListAdapter<HomeProducts, ListViewAdapter.ListViewHolder>(HomeProductsDiffUtils()) {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RvShopProductItemBinding.bind(itemView)
    }
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_shop_product_item, parent, false))
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            productName.text = item.title
            productPrice.text = item.maxPrice
            productBrand.text = item.brand
            Glide.with(productImg).load(item.image).into(productImg)
            root.setOnClickListener {
                handleAction.goToDetails(item.id!!)
            }
        }
    }
}