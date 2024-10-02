package com.iti4.retailhub.features.mybag

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.databinding.RvMybagItemBinding

class DiffUtilProduct : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}

class MyBagRecyclerViewAdapter() :
    ListAdapter<Product, MyBagRecyclerViewAdapter.ViewHolder>(DiffUtilProduct()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvMybagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            ivMyBagProductImage.setImageResource(item.image)
            tvMyBagProductName.text = item.title
            tvMyBagProductPrice.text = item.price.toString()
            tvMyBagProductColor.text = item.color
            tvMyBagProductSize.text = item.size
        }

    }
        inner class ViewHolder(val binding: RvMybagItemBinding) :
            RecyclerView.ViewHolder(binding.root)


    }