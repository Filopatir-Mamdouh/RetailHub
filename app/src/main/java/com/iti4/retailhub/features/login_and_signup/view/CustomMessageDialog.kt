package com.iti4.retailhub.features.login_and_signup.view

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.iti4.retailhub.R
class CustomMessageDialog(context: Context) : Dialog(context) {

    private var loadingTextView: TextView

    init {
        setContentView(R.layout.message_dialog)
        setCancelable(true)
        findViewById<View>(R.id.dialog_layout).setOnClickListener { dismiss() } // Dismiss on click
        findViewById<View>(R.id.button).setOnClickListener { dismiss() } // Dismiss on click
        loadingTextView = findViewById(R.id.messageTextCustomDialog)
    }

    fun setText(text: String) {
        loadingTextView.text = text
    }
}