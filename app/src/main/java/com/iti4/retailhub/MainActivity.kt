package com.iti4.retailhub

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.iti4.retailhub.loginandsignup.viewmodel.UserAuthunticationViewModelViewModel

class MainActivity : AppCompatActivity() {
    val userAuthViewModel: UserAuthunticationViewModelViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            userAuthViewModel.signOut()
            finish()
        }
    }
}