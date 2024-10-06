package com.iti4.retailhub.features.mybag

import android.content.Context
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
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.RvMybagProductItemBinding
import com.iti4.retailhub.models.CartProduct


class DiffUtilProduct : DiffUtil.ItemCallback<CartProduct>() {
    override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
        return oldItem == newItem
    }
}

class MyBagProductRecyclerViewAdapter(val handleActions: OnClickMyBag) :
    ListAdapter<CartProduct, MyBagProductRecyclerViewAdapter.ViewHolder>(DiffUtilProduct()) {

    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RvMybagProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            Glide.with(context!!)
                .load(item.itemImage)
                .into(ivMyBagProductImage)
            val quantity = item.itemQuantity
            val inventoryQuantity = item.inventoryQuantity


            tvMyBagProductName.text = item.itemTitle
            tvMyBagProductPrice.text = item.itemPrice + " EGP"
            tvMyBagProductColor.text = item.itemColor
            tvMyBagProductSize.text = item.itemSize

            if (inventoryQuantity!! < 1)
                tvMyBagProductCount.text = "Out of Stock"

            if (quantity > inventoryQuantity)
                tvMyBagProductCount.text = inventoryQuantity.toString()
            else
                tvMyBagProductCount.text = quantity.toString()


            btnMyBagPopmenu.setOnClickListener {
                showPopupMenu(btnMyBagPopmenu, item)
            }

            btnMyBagAdd.setOnClickListener {
                val currentCountString = tvMyBagProductCount.text.toString()
                val currentCount = Integer.parseInt(currentCountString) + 1
                if (currentCount > inventoryQuantity) {
                    tvMaxReached.visibility = View.VISIBLE
                } else{
                    item.itemQuantity = currentCount
                    item.didQuantityChanged=true
                    handleActions.updateTotalPrice()
                    tvMyBagProductCount.text = (currentCount).toString()
                }
            }

            btnMyBagRemove.setOnClickListener {
                val currentCountString = tvMyBagProductCount.text.toString()
                val currentCount = Integer.parseInt(currentCountString) - 1
                if (currentCount in 1..<inventoryQuantity) {
                    tvMaxReached.visibility = View.GONE
                }
                if (currentCount >= 1) {
                    item.itemQuantity = currentCount
                    item.didQuantityChanged=true
                    handleActions.updateTotalPrice()
                    tvMyBagProductCount.text = (currentCount).toString()
                }
            }

        }
    }

    inner class ViewHolder(val binding: RvMybagProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun showPopupMenu(view: View, cartProduct: CartProduct) {
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
                    val currentList = currentList.toMutableList()
                    currentList.remove(cartProduct)
                    submitList(currentList)
                    handleActions.deleteMyBagItem(cartProduct)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

}