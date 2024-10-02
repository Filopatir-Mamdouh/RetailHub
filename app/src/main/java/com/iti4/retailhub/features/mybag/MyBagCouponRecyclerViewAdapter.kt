package com.iti4.retailhub.features.mybag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.databinding.RvMybagCoupounItemBinding

class DiffUtilPromo : DiffUtil.ItemCallback<Promo>() {
    override fun areItemsTheSame(oldItem: Promo, newItem: Promo): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Promo, newItem: Promo): Boolean {
        return oldItem == newItem
    }
}

class MyBagCouponRecyclerViewAdapter( private val onClickApply: OnClickApply) :
    ListAdapter<Promo, MyBagCouponRecyclerViewAdapter.ViewHolder>(DiffUtilPromo()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RvMybagCoupounItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvMyBagPromoTitle.text = item.title
            tvMyBagPromoCode.text = item.code
            tvMyBagPromoRemainingTime.text = item.remainingTime
            btnMyBagPromoApply.setOnClickListener{
                onClickApply.onClickApply(item)
            }
        }

    }


    inner class ViewHolder(val binding: RvMybagCoupounItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}