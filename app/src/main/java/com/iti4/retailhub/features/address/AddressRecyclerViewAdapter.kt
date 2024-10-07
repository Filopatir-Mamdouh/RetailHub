package com.iti4.retailhub.features.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.GetAddressesByIdQuery
import com.iti4.retailhub.databinding.RvAddressesItemBinding
import com.iti4.retailhub.models.CustomerAddress


class DiffUtilAddresses : DiffUtil.ItemCallback<GetAddressesByIdQuery.Address>() {
    override fun areItemsTheSame(
        oldItem: GetAddressesByIdQuery.Address, newItem: GetAddressesByIdQuery.Address
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GetAddressesByIdQuery.Address, newItem: GetAddressesByIdQuery.Address
    ): Boolean {
        return oldItem == newItem
    }
}

class AddressRecyclerViewAdapter(val handleAction: OnClickAddress) :
    ListAdapter<GetAddressesByIdQuery.Address, AddressRecyclerViewAdapter.ViewHolder>(
        DiffUtilAddresses()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RvAddressesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvAddressName.text = item.name
            tvAddress1Add.text = item.address1
            tvAddress2Add.text = item.address2 + "," + item.city + "," + item.country
            tvAddressPhone.text = item.phone
            tvAddressEdit.setOnClickListener {
                handleAction.editDetails(
                    CustomerAddress(
                        item.address1 ?: " ",
                        item.address2 ?: "empty",
                        item.phone ?: " ",
                        item.name ?: " ",
                        false,
                        id = item.id
                    )
                )
            }
        }
    }


    inner class ViewHolder(val binding: RvAddressesItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}