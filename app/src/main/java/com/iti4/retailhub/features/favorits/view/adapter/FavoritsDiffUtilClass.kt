package com.iti4.retailhub.features.favorits.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.ProductDetailsQuery

class FavoritsDiffUtilClass : DiffUtil.ItemCallback<GetCustomerFavoritesQuery.Node>() {
    override fun areItemsTheSame(oldItem: GetCustomerFavoritesQuery.Node, newItem: GetCustomerFavoritesQuery.Node): Boolean {
        return oldItem.namespace.substringAfterLast("/") == newItem.namespace.substringAfterLast("/")
    }

    override fun areContentsTheSame(oldItem: GetCustomerFavoritesQuery.Node, newItem: GetCustomerFavoritesQuery.Node): Boolean {
        return oldItem == newItem
    }

}
