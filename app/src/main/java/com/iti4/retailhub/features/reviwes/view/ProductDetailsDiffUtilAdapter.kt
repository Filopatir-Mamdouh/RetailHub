package com.iti4.retailhub.features.reviwes.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.ProductDetailsRecycleviewItemBinding
import com.iti4.retailhub.databinding.ReviewsRecycleItemBinding
import com.iti4.retailhub.models.Review


class ReviewsDiffUtilAdapter(private val context: Context) : ListAdapter<Review, ReviewsDiffUtilAdapter.ViewHolder>(ReviewsDiffUtilClass()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReviewsRecycleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.reviewname.text = item.name
        holder.binding.reviewrating.rating = item.rate
        holder.binding.reviewcommet.text = item.comment
        Glide.with(context)
            .load(item.imageUrl)
            .error(android.R.drawable.stat_notify_error)
            .into((holder.binding.profileImage))

    }

    class ViewHolder(val binding: ReviewsRecycleItemBinding) : RecyclerView.ViewHolder(binding.root)
}