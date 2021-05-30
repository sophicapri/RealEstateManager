package com.sophieoc.realestatemanager

import android.app.Application
import com.sophieoc.realestatemanager.util.PreferenceHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceHelper.initPreferenceHelper(this)
    }
}