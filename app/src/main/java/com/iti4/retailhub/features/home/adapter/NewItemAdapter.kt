package com.iti4.retailhub.features.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.NewCardItemBinding
import com.iti4.retailhub.features.home.OnClickGoToDetails
import com.iti4.retailhub.models.HomeProducts

class NewItemAdapter(val handleAction:OnClickGoToDetails,val favoritList: List<GetCustomerFavoritesQuery.Node>) : ListAdapter<HomeProducts, NewItemAdapter.ViewHolder>(HomeProductsDiffUtils()) {
    lateinit var context: Context
    var isAddToFavoritesFirstClick=true

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = NewCardItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(View.inflate(parent.context, R.layout.new_card_item, null).rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        val isFavorite = favoritList.any { it.value == item.id }
        if (isFavorite) {
            holder.binding.favBtn.setImageResource(R.drawable.fav_filled)

        }
            holder.binding.favBtn.setOnClickListener{
                if(isAddToFavoritesFirstClick) {
                    handleAction.saveToFavorites(
                        "", item.id!!,
                        "", "",
                        item.title!!, item.image,
                        buildString {
                            append(item.maxPrice)
                            append(" ")
                            append(item.currencyCode)
                        }
                    )
            }
        }
        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(item.image).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter()
                .apply(RequestOptions().override(150, 200))
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView)
//            Glide.with(holder.itemView).load(item.image).centerCrop().into(imageView)
            textView7.text = item.brand
            textView8.text = item.title
            newItemPrice.text = buildString {
                append(item.maxPrice)
                append(" ")
                append(item.currencyCode)
            }

            root.setOnClickListener{
                handleAction.goToDetails(item.id!!)
            }
        }
    }
}