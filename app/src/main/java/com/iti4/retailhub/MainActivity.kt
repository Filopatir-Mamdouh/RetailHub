package com.iti4.retailhub

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.iti4.retailhub.communicators.ToolbarController
import com.iti4.retailhub.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ToolbarController {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(
                this,
                R.id.fragmentContainerView2
            ).navigateUp()
        }

        supportActionBar?.title = ""
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navigationView.setupWithNavController(
            Navigation.findNavController(
                this,
                R.id.fragmentContainerView2
            )
        )
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                // Collapsed
                actionBar?.setBackgroundDrawable(getResources().getColor(R.color.white) as Drawable)
                binding.pageName.visibility = View.GONE
                binding.collapsedPageName.visibility = View.VISIBLE
            } else {
                // Expanded
                actionBar?.setBackgroundDrawable(getResources().getColor(R.color.background_color) as Drawable)
                binding.pageName.visibility = View.VISIBLE
                binding.collapsedPageName.visibility = View.GONE
            }
        }

    }

    override fun setVisibility(visibility: Boolean) {
        binding.apply {
            appBar.visibility = if (visibility) View.VISIBLE else View.GONE
            toolbar.visibility = if (visibility) View.VISIBLE else View.GONE
            actionBar?.hide()
            collapsingToolbar.visibility = if (visibility) View.VISIBLE else View.GONE
        }
    }

    override fun setTitle(title: String) {
        binding.apply {
            pageName.text = title
            collapsedPageName.text = title
        }
    }

    override fun collapse() {
        binding.appBar.setExpanded(false, false);
    }

    override fun expand() {
        binding.appBar.setExpanded(true, true);
    }

    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.fragmentContainerView2)
        if (navController.previousBackStackEntry != null) {
            navController.navigateUp()
        } else {
            super.onBackPressed()
        }
    }



}
