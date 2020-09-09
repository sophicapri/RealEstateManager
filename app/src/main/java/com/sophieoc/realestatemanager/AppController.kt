package com.sophieoc.realestatemanager

import android.app.Application
import android.location.Location
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppController: Application() {
    var currentLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidContext(this@AppController)
            modules(listOf(appModule))
        }
    }

    companion object {
        lateinit var instance: AppController
            private set
    }
}