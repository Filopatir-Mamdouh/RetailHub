package com.iti4.retailhub.features.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.CollectionsQuery
import com.iti4.retailhub.ProductsQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.NewCardItemBinding

class NewItemAdapter : ListAdapter<ProductsQuery.Edge, NewItemAdapter.ViewHolder>(NewItemUtils()) {
    lateinit var context : Context
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = NewCardItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewItemAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(View.inflate(parent.context, R.layout.new_card_item, null).rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            Glide.with(holder.itemView).load(item.node.media.nodes[0].onMediaImage?.image?.url).into(imageView)
            textView7.text = item.node.vendor
            textView8.text = item.node.title
            textView10.text = buildString {
                append(item.node.priceRangeV2.maxVariantPrice.amount)
                append(" EGP")
            }
        }
    }
}