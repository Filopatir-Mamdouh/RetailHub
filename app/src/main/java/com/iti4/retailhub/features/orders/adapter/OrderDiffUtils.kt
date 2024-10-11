package com.iti4.retailhub.features.orders.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.models.Order

class OrderDiffUtils : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}