package com.iti4.retailhub.features.shop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.R
import com.iti4.retailhub.constants.CategoriesName
import com.iti4.retailhub.databinding.ShopCategoryTabItemBinding
import com.iti4.retailhub.models.Category

class ShopAdapter : ListAdapter<Category, ShopAdapter.ViewHolder>(CategoryDiffUtils()){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ShopCategoryTabItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.shop_category_tab_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.shopCategoryRV.apply {
            adapter = CategoryItemAdapter(item.productTypes, item.id)
        }
    }
}