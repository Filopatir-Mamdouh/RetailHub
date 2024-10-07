package com.iti4.retailhub.features.productdetails.view


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.iti4.retailhub.R

class CustomSpinnerAdapter(
    private val context: Context,
    private val items: List<String>,
    private val images: IntArray
): BaseAdapter() {

    override fun getCount(): Int {
        return items.size ?: 0
    }

    override fun getItem(p0: Int): Any {
        return 1
    }

    override fun getItemId(p0: Int): Long {
        return 1
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_item, p2, false)
        val textView = view.findViewById<TextView>(R.id.textView)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        textView.text = items[p0]
        imageView.setImageResource(images[p0])
        return view
    }


}