package com.example.basedroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.basedroid.ui.NewsViewModel

class NetworkReceiver(private val viewModel: NewsViewModel) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!viewModel.checkNetworkAvailability()){
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
        }
    }
}