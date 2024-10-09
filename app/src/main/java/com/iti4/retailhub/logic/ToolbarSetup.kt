package com.iti4.retailhub.logic

import android.content.res.Resources
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import com.iti4.retailhub.R
import com.iti4.retailhub.databinding.AppBarBinding
import kotlin.math.abs

object ToolbarSetup {
    fun setupToolbar(appBarBinding: AppBarBinding, title:String, resources: Resources , navController: NavController ){
        appBarBinding.apply {
            pageName.text = title
            collapsedPageName.text = title
            backButton.setOnClickListener {
                navController.navigateUp()
            }
            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val totalScrollRange = appBarLayout.totalScrollRange
                if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                    // Collapsed
                    toolbar.background = ResourcesCompat.getDrawable(resources, R.color.white, null)
                    pageName.visibility = View.GONE
                    collapsedPageName.visibility = View.VISIBLE
                } else {
                    // Expanded
                    toolbar.background = ResourcesCompat.getDrawable(resources, R.color.white, null)
                    pageName.visibility = View.VISIBLE
                    collapsedPageName.visibility = View.GONE
                }
            }
        }
    }
}