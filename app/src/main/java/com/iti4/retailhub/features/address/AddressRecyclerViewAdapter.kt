package com.iti4.retailhub.features.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.CustomDialog
import com.iti4.retailhub.databinding.RvAddressesItemBinding
import com.iti4.retailhub.models.CustomerAddress


class DiffUtilAddresses : DiffUtil.ItemCallback<CustomerAddress>() {
    override fun areItemsTheSame(
        oldItem: CustomerAddress, newItem: CustomerAddress
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CustomerAddress, newItem: CustomerAddress
    ): Boolean {
        return oldItem == newItem
    }
}

class AddressRecyclerViewAdapter(val handleAction: OnClickAddress) :
    ListAdapter<CustomerAddress, AddressRecyclerViewAdapter.ViewHolder>(
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
            tvAddress2Add.text = item.address2
            tvAddressPhone.text = item.phone
            tvAddressEdit.setOnClickListener {
                handleAction.editDetails(
                    item
                )
            }
            btnDeleteAddress.setOnClickListener {
                val dialog =
                    CustomDialog(it.context, handleAction, null, "address")
                dialog.show()
                dialog.getData(item.id ?: "null")

            }
        }
    }


    inner class ViewHolder(val binding: RvAddressesItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}