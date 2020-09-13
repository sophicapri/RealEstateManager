package com.sophieoc.realestatemanager.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper {
    companion object {
        const val SHARED_PREFS = "sharedPrefs"
        private var sharedPrefs: SharedPreferences? = null
        lateinit var uid : String

        fun initPreferenceHelper(context: Context) {
            sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        }
    }
}
