package com.iti4.retailhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.iti4.retailhub.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
