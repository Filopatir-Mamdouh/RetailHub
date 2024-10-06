package com.example.weathercast.alarmandnotification.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.ProductDetailsRecycleviewItemBinding


class ProductDetailsDiffUtilAdapter(private val context: Context) : ListAdapter<ProductDetailsQuery.Edge2, ProductDetailsDiffUtilAdapter.ViewHolder>(ProductDetailsDiffUtilClass()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductDetailsRecycleviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Glide.with(context)
            .load(item.node.url.toString().split("?")[0])
            .error(android.R.drawable.stat_notify_error)
            .into((holder.binding.productImage))

    }

    class ViewHolder(val binding: ProductDetailsRecycleviewItemBinding) : RecyclerView.ViewHolder(binding.root)
}