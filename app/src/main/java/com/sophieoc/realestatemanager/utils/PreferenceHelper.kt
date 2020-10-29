package com.sophieoc.realestatemanager.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper {
    companion object {
        private const val SHARED_PREFS = "sharedPrefs"
        private var sharedPrefs: SharedPreferences? = null
        var internetAvailable : Boolean = false
        var locationEnabled : Boolean = false
        lateinit var currentUserId : String

        fun initPreferenceHelper(context: Context) {
            sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        }
    }
}