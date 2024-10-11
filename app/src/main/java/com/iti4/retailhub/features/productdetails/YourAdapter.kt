package com.iti4.retailhub.features.productdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.iti4.retailhub.ProductDetailsQuery
import com.iti4.retailhub.R


class ViewAdapter(private val context: Context,private val images: List<ProductDetailsQuery.Edge2>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater

        val view: View = layoutInflater!!.inflate(R.layout.product_details_recycleview_item, null)
        val imageView = view.findViewById<ImageView>(R.id.productImage)

        Glide.with(context)
            .load(images[position].node.url.toString().split("?")[0])
            .error(android.R.drawable.stat_notify_error)
            .into(imageView)


        val viewPager = container as ViewPager
        viewPager.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}
