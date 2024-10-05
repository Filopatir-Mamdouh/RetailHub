package com.iti4.retailhub.features.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.models.Brands

class BrandUtils : DiffUtil.ItemCallback<Brands>(){
    override fun areItemsTheSame(oldItem: Brands, newItem: Brands): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Brands, newItem: Brands): Boolean {
        return oldItem == newItem
    }
}
