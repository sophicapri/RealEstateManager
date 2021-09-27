package com.sophieoc.realestatemanager.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    val todayDate: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            return dateFormat.format(Date())
        }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected: Boolean
        isConnected = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnectedOrConnecting
        } else {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return isConnected
    }

    fun hideStatusBar(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = activity.window.insetsController
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller?.hide(WindowInsets.Type.statusBars())
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}