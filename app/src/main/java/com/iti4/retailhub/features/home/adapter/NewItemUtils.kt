package com.iti4.retailhub.features.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.models.HomeProducts

class NewItemUtils : DiffUtil.ItemCallback<HomeProducts>(){
    override fun areItemsTheSame(oldItem: HomeProducts, newItem: HomeProducts): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HomeProducts, newItem: HomeProducts): Boolean {
        return oldItem == newItem
    }
}