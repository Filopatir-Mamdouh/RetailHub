package com.iti4.retailhub.features.address


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.databinding.RvAddressSearchItemBinding
import com.iti4.retailhub.modelsdata.PlaceLocation


class AddressLookupRecyclerViewAdapter(
    private var data: MutableList<PlaceLocation>, val actionHandler: OnClickMap
) :
    RecyclerView.Adapter<AddressLookupRecyclerViewAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RvAddressSearchItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        with(holder.binding) {
            text1.text =
                item.address.name + " " + (item.address.city
                    ?: " ") + " " + (item.address.country_code ?: " ")
            root.setOnClickListener {
                actionHandler.goToAddress(item)
                this@AddressLookupRecyclerViewAdapter.data = mutableListOf()
                notifyDataSetChanged()

            }
        }
    }


    fun setData(data: MutableList<PlaceLocation>) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: RvAddressSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}