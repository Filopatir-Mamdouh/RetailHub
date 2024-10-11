package com.iti4.retailhub.features.productdetails.view.bottom_dialog_adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.databinding.BottomDialogDetailsRvBinding
import com.iti4.retailhub.databinding.ProductDetailsBottomSheetBinding
import com.iti4.retailhub.databinding.ProductDetailsRecycleviewItemBinding


class BottomDialogDiffUtilAdapter(
    private val type: String,
    private val onClick: ButtomDialogOnClickListn,
    private val selectedItem:String) : ListAdapter<String, BottomDialogDiffUtilAdapter.ViewHolder>(BottomDialogDiffUtilClass()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BottomDialogDetailsRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("onBindViewHolder", "onBindViewHolder:$selectedItem ")
        holder.binding.textView31.text = item
        if(selectedItem!=item) {
            holder.binding.cardView3.setOnClickListener {
                onClick.choosedItem(position, item, type)
            }
        }else{
            holder.binding.textView31.setTextColor(holder.binding.root.context.getColor(com.iti4.retailhub.R.color.white))
            holder.binding.cardView3.setCardBackgroundColor(holder.binding.root.context.getColor(com.iti4.retailhub.R.color.red_color))
        }


    }

    class ViewHolder(val binding: BottomDialogDetailsRvBinding) : RecyclerView.ViewHolder(binding.root)
}