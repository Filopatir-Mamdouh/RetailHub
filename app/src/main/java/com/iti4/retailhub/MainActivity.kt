package com.iti4.retailhub

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.iti4.retailhub.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.navigationView.setupWithNavController(Navigation.findNavController(this,R.id.fragmentContainerView2))
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (abs(verticalOffset.toDouble()) >= totalScrollRange) {
                // Collapsed
                binding.pageName.visibility = View.GONE
                binding.collapsedPageName.visibility = View.VISIBLE
            } else {
                // Expanded
                binding.pageName.visibility = View.VISIBLE
                binding.collapsedPageName.visibility = View.GONE
            }
        }
    }
}