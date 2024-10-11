package com.iti4.retailhub.features.favorits.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.databinding.FavoritsRecycleItemBinding


class FavoritsDiffUtilAdapter(private val context: Context,private val listener: OnFavoritItemClocked) : ListAdapter<GetCustomerFavoritesQuery.Node, FavoritsDiffUtilAdapter.ViewHolder>(
    FavoritsDiffUtilClass()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoritsRecycleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val description=item.description?.split(",")
        val productTitleList = description?.get(0)?.split("|")
        Glide.with(context)
            .load(description?.get(description.size-2)?.substringBefore("?"))
            .error(android.R.drawable.stat_notify_error)
            .into((holder.binding.favoritimag))
        holder.binding.favoritItem.setOnClickListener {
            val productID=item.value
            listener.onShowFavoritItemDetails(productID)
        }
        holder.binding.favoritsProductName2.text=productTitleList?.get(0)
        holder.binding.favoritProductName.text=productTitleList?.drop(1)?.joinToString(" | ")

        holder.binding.favoritsProductPrice.text=description?.get(description.size-1)
        holder.binding.favoritdelete.setOnClickListener{
            listener.showDeleteAlert(item.id)
        }

    }

    class ViewHolder(val binding: FavoritsRecycleItemBinding) : RecyclerView.ViewHolder(binding.root)
}