package com.iti4.retailhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.iti4.retailhub.databinding.ActivityMainBinding
import com.iti4.retailhub.features.address.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: AddressViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navigationView.apply {
            setOnItemSelectedListener { menuItem ->
                val navController = Navigation.findNavController(this@MainActivity, R.id.fragmentContainerView2)
                if (menuItem.itemId != navController.currentDestination?.id) {
                    Log.d("Filo", "Navigating to: ${menuItem.itemId}")
                    navController.navigate(menuItem.itemId, null,
                        NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build())
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.fragmentContainerView2)
        if (navController.previousBackStackEntry != null) {
            navController.navigateUp()
            if(navController.currentDestination?.id == R.id.homeFragment){
                binding.navigationView.menu[0].isChecked = true
            }
        } else {
            super.onBackPressed()
        }
    }


    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = currentFocus // Get the currently focused view
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
    fun hideBottomNavBar(){
        binding.navigationView.visibility = View.GONE
    }
    fun showBottomNavBar(){
        binding.navigationView.visibility = View.VISIBLE
    }


}
