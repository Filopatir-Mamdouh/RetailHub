package com.iti4.retailhub.features.shop.search.adapter

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.RvShopGridItemBinding
import com.iti4.retailhub.features.home.OnClickGoToDetails
import com.iti4.retailhub.features.home.adapter.HomeProductsDiffUtils
import com.iti4.retailhub.models.HomeProducts

class GridViewAdapter(private val handleAction: OnClickGoToDetails) :  ListAdapter<HomeProducts, GridViewAdapter.ListViewHolder>(
    HomeProductsDiffUtils()
) {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RvShopGridItemBinding.bind(itemView)
    }
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_shop_grid_item, parent, false))
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            Glide.with(holder.itemView).load(item.image)
                .into(imageView)
            textView7.text = item.brand
            textView8.text = item.title?.split("|")?.get(1)
            newItemPrice.text = buildString {
                append(item.maxPrice)
                append(" ")
                append(item.currencyCode)
            }
            root.setOnClickListener{
                handleAction.goToDetails(item.id!!)
            }
        }
    }
}