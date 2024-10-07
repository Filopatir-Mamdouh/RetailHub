package com.iti4.retailhub.features.favorits.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.databinding.FavoritsRecycleItemBinding
import com.iti4.retailhub.databinding.ProductDetailsRecycleviewItemBinding


class FavoritsDiffUtilAdapter(private val context: Context,private val listener: OnFavoritItemClocked) : ListAdapter<GetCustomerFavoritesQuery.Node, FavoritsDiffUtilAdapter.ViewHolder>(
    FavoritsDiffUtilClass()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoritsRecycleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        Log.d("fav", "onBindViewHolder: ${item.description?.get(0)}")
        val description=item.description?.split(",")
        Log.d("fav", "onBindViewHolder: ${description?.get(3)?.substringBefore("?")}")
        Log.d("fav", "onBindViewHolder:${description?.get(4)} ")
        Log.d("fav", "onBindViewHolder:${description?.get(0)} ")
        Log.d("fav", "onBindViewHolder:${description?.get(0)?.get(1)} ${description?.get(0)?.get(2)} ")

        Log.d("fav", "onBindViewHolder:${description?.get(2)} ")
        Log.d("fav", "onBindViewHolder:${description?.get(1)} ")
        Glide.with(context)
            .load(description?.get(description.size-2)?.substringBefore("?"))
            .error(android.R.drawable.stat_notify_error)
            .into((holder.binding.favoritimag))
        holder.binding.favoritsProductColor.text=description?.get(description.size-4)
        holder.binding.favoritProductSize.text=description?.get(description.size-3)
        holder.binding.favoritItem.setOnClickListener {
            listener.onShowFavoritItemDetails(item.namespace)
        }
        holder.binding.favoritProductName.text="${description?.get(1)}${description?.get(2)}"

        holder.binding.favoritsProductName2.text=description?.get(0)

        holder.binding.favoritsProductPrice.text=description?.get(description.size-1)

    }

    class ViewHolder(val binding: FavoritsRecycleItemBinding) : RecyclerView.ViewHolder(binding.root)
}