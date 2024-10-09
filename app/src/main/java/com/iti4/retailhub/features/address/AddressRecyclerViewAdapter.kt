package com.iti4.retailhub.features.address

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iti4.retailhub.CustomDialog
import com.iti4.retailhub.databinding.RvAddressesItemBinding
import com.iti4.retailhub.models.CustomerAddress


class AddressRecyclerViewAdapter(
    val handleAction: OnClickAddress, var listData: MutableList<CustomerAddress>
) :
    RecyclerView.Adapter<AddressRecyclerViewAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RvAddressesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData.get(position)
        with(holder.binding) {
            Log.i("here", "onBindViewHolder: " + item.name + item.isDefault)
            if (item.isDefault) {
                btnDeleteAddress.visibility = View.INVISIBLE
                cbAddressDefault.visibility = View.VISIBLE
                cbAddressDefault.isChecked = true
            } else {
                btnDeleteAddress.visibility = View.VISIBLE
                cbAddressDefault.isChecked = false
                cbAddressDefault.visibility = View.VISIBLE
            }
            // if checked do this if not do this dont let it clickable if checked
            cbAddressDefault.setOnCheckedChangeListener { e, f ->
                handleAction.setDefaultAddress(position)
            }
            tvAddressName.text = item.name
            tvAddress1Add.text = item.address1
            tvAddress2Add.text = item.address2
            tvAddressPhone.text = item.phone
            tvAddressEdit.setOnClickListener {
                handleAction.editDetails(item)
            }
            btnDeleteAddress.setOnClickListener {
                val dialog =
                    CustomDialog(it.context, handleAction, null, "address")
                dialog.show()
                dialog.getData(item.id ?: "null")

            }
            root.setOnClickListener {
                handleAction.setDefaultAddress(position)
            }
        }
    }


    inner class ViewHolder(val binding: RvAddressesItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}