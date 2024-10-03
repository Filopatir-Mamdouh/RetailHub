package com.iti4.retailhub.features.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.ProductsQuery

class NewItemUtils : DiffUtil.ItemCallback<ProductsQuery.Edge>(){
    override fun areItemsTheSame(
        oldItem: ProductsQuery.Edge,
        newItem: ProductsQuery.Edge
    ): Boolean {
        return oldItem.node.id == newItem.node.id
    }

    override fun areContentsTheSame(
        oldItem: ProductsQuery.Edge,
        newItem: ProductsQuery.Edge
    ): Boolean {
        return oldItem == newItem
    }
}