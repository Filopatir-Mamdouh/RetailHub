package com.iti4.retailhub.features.mybag

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
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.RvMybagProductItemBinding

class DiffUtilProduct : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}

class MyBagProductRecyclerViewAdapter() :
    ListAdapter<Product, MyBagProductRecyclerViewAdapter.ViewHolder>(DiffUtilProduct()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvMybagProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            ivMyBagProductImage.setImageResource(item.image)
            tvMyBagProductName.text = item.title
            tvMyBagProductPrice.text = item.price.toString() + "$"
            tvMyBagProductColor.text = item.color
            tvMyBagProductSize.text = item.size
            tvMyBagProductCount.text = 1.toString()

            btnMyBagPopmenu.setOnClickListener {
                showPopupMenu(btnMyBagPopmenu)
            }

            btnMyBagAdd.setOnClickListener {
                val currentCountString = tvMyBagProductCount.text.toString()
                val currentCount = Integer.parseInt(currentCountString)
                tvMyBagProductCount.text = (currentCount + 1).toString()
            }

            btnMyBagRemove.setOnClickListener {
                val currentCountString = tvMyBagProductCount.text.toString()
                val currentCount = Integer.parseInt(currentCountString)
                if (currentCount > 1) {
                    tvMyBagProductCount.text = (currentCount - 1).toString()
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