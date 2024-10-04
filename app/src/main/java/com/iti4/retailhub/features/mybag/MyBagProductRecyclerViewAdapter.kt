package com.iti4.retailhub.features.mybag

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.GetDraftOrdersByCustomerQuery
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.RvMybagProductItemBinding

class DiffUtilProduct : DiffUtil.ItemCallback<GetDraftOrdersByCustomerQuery.Node1>() {
    override fun areItemsTheSame(oldItem: GetDraftOrdersByCustomerQuery.Node1, newItem: GetDraftOrdersByCustomerQuery.Node1): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: GetDraftOrdersByCustomerQuery.Node1, newItem: GetDraftOrdersByCustomerQuery.Node1): Boolean {
        return oldItem == newItem
    }
}

class MyBagProductRecyclerViewAdapter() :
    ListAdapter<GetDraftOrdersByCustomerQuery.Node1, MyBagProductRecyclerViewAdapter.ViewHolder>(DiffUtilProduct()) {
    var context : Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val binding = RvMybagProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            Glide.with(context!!)
                .load( item.variant?.product?.media?.nodes?.get(0)?.onMediaImage?.image?.url as String)
                .into(ivMyBagProductImage)
            val quantity = item.quantity
            val inventoryQuantity = item.variant.inventoryQuantity


            tvMyBagProductName.text = item.title
            tvMyBagProductPrice.text = item.variant?.price.toString() + "$"
            tvMyBagProductColor.text = item.variant?.selectedOptions?.get(1)?.value
            tvMyBagProductSize.text = item.variant?.selectedOptions?.get(0)?.value

            if(inventoryQuantity!!<1)
                tvMyBagProductCount.text ="Out of Stock"

            if(quantity>inventoryQuantity)
                tvMyBagProductCount.text = inventoryQuantity.toString()


            btnMyBagPopmenu.setOnClickListener {
                showPopupMenu(btnMyBagPopmenu)
            }

            btnMyBagAdd.setOnClickListener {
                val currentCountString = tvMyBagProductCount.text.toString()
                val currentCount = Integer.parseInt(currentCountString)+1
                if(currentCount>inventoryQuantity ){
                    tvMyBagProductCount.text = inventoryQuantity.toString()
                    tvMaxReached.visibility=View.VISIBLE
                }
                else
                tvMyBagProductCount.text = (currentCount).toString()
            }

            btnMyBagRemove.setOnClickListener {
                val currentCountString = tvMyBagProductCount.text.toString()
                val currentCount = Integer.parseInt(currentCountString)-1
                if(currentCount<inventoryQuantity && currentCount>0){
                    tvMaxReached.visibility=View.GONE
                }
                if (currentCount >= 1) {
                    tvMyBagProductCount.text = (currentCount).toString()
                }
            }

        }
    }

    inner class ViewHolder(val binding: RvMybagProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(view.context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.mybag_popupmenu, popup.menu)


        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.item1 -> {
                    Toast.makeText(view.context, "Option 1 clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.item2 -> {
                    Toast.makeText(view.context, "Option 2 clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

}