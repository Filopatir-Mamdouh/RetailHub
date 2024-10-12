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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.iti4.retailhub.databinding.ActivityMainBinding
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.address.AddressViewModel
import com.iti4.retailhub.features.login_and_signup.viewmodel.UserAuthunticationViewModelViewModel
import com.iti4.retailhub.models.CurrencyResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Response


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: AddressViewModel by viewModels()
    private val viewModel by viewModels<MainActivityViewModel>()
    val userAuthViewModel: UserAuthunticationViewModelViewModel by viewModels<UserAuthunticationViewModelViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getDiscount()
        if (!userAuthViewModel.isguestMode()){
            viewModel.getUsedDiscounts()
        }
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

        if (viewModel.getFirstTime() || viewModel.getShouldIRefrechCurrency()) {
            viewModel.setFirstTime()
            viewModel.setRefrechCurrency()
            viewModel.getCurrencyRates()
            initCurrencyRatesListen()
        }
    }


    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.fragmentContainerView2)
        if (navController.previousBackStackEntry != null) {

            navController.navigateUp()
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

    fun hideBottomNavBar() {
        binding.navigationView.visibility = View.GONE;
    }

    fun showBottomNavBar() {
        binding.navigationView.visibility = View.VISIBLE;
    }

    fun initCurrencyRatesListen() {
        lifecycleScope.launch {
            viewModel.currencyState.collect { item ->
                when (item) {
                    is ApiState.Success<*> -> {
                        if (item.data != null) {
                            val response = item.data as Response<CurrencyResponse>
                            if (response.isSuccessful) {
                                val datas = response.body()!!.conversion_rates
                                viewModel.saveConversionRates(datas)
                            }
                        }
                    }

                    is ApiState.Error -> {
                        Log.i("here", "error: ")
                    }

                    is ApiState.Loading -> {}
                }
            }

        }
    }
}


