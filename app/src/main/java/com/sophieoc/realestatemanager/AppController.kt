package com.sophieoc.realestatemanager

import android.app.Application
import android.location.Location

class AppController: Application() {
    var currentLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AppController
            private set
    }
}