package com.iti4.retailhub.features.orders.orderdetails.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.models.OrderDetailsItem

class OrderDetailsItemUtils : DiffUtil.ItemCallback<OrderDetailsItem>() {
    override fun areItemsTheSame(oldItem: OrderDetailsItem, newItem: OrderDetailsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OrderDetailsItem, newItem: OrderDetailsItem): Boolean {
        return oldItem == newItem
    }
}