package com.iti4.retailhub

import android.app.Application
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51Q5iOxB2VRlrbgQ7sEVPYTMVmfplCmtGo5EibD95SbNMis5QoW8IMzkHCloTbbx6uS89wToh9Z3AOqBRF431i44m00nXQ3rTn9"
        )
    }
}
