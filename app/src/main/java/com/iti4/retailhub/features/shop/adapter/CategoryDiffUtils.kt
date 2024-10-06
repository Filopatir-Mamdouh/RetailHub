package com.iti4.retailhub.features.shop.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.models.Category

class CategoryDiffUtils : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}