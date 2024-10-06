package com.example.weathercast.alarmandnotification.view

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.ProductDetailsQuery

class ProductDetailsDiffUtilClass : DiffUtil.ItemCallback<ProductDetailsQuery.Edge2>() {
    override fun areItemsTheSame(oldItem: ProductDetailsQuery.Edge2, newItem: ProductDetailsQuery.Edge2): Boolean {
        return oldItem.node.url.toString().split("?")[1] == newItem.node.url.toString().split("?")[1]
    }

    override fun areContentsTheSame(oldItem: ProductDetailsQuery.Edge2, newItem: ProductDetailsQuery.Edge2): Boolean {
        return oldItem == newItem
    }

}
