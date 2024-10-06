package com.iti4.retailhub.features.reviwes.view

import androidx.recyclerview.widget.DiffUtil
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.models.Review

class ReviewsDiffUtilClass : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }

}
