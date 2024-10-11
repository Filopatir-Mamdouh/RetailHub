package com.iti4.retailhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.iti4.retailhub.databinding.ActivityMainBinding
import com.iti4.retailhub.logic.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val _isConnectedToNetwork = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val isConnectedToNetwork =_isConnectedToNetwork.onStart { checkNetwork() }.stateIn(lifecycleScope, SharingStarted.Eagerly, false)
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
        val fragments = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2)?.childFragmentManager?.fragments
        lifecycleScope.launch {
            isConnectedToNetwork.collect{ networkState ->
                if(networkState){
                    fragments?.forEach{
                        it.view?.isEnabled = true
                    }
                    findViewById<View>(R.id.fragmentContainerView2).visibility = View.VISIBLE
                    binding.apply{
                        navigationView.menu.setGroupEnabled(R.id.navGroup, true)
                        networkAnimView.visibility = View.GONE
                    }
                }else{
                    fragments?.forEach{
                        it.view?.isEnabled = false
                    }
                    findViewById<View>(R.id.fragmentContainerView2).visibility = View.GONE
                    binding.apply{
                        navigationView.menu.setGroupEnabled(R.id.navGroup, false)
                        networkAnimView.visibility = View.VISIBLE
                    }
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

    private fun checkNetwork() {
        lifecycleScope.launch {
            while (true) {
                _isConnectedToNetwork.emit(NetworkUtils.isNetworkAvailable(this@MainActivity))
                delay(3000)
            }
        }
    }
}
