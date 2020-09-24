package com.sophieoc.realestatemanager

import android.app.Application
import android.location.Location
import com.sophieoc.realestatemanager.utils.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppController: Application() {
    var currentLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        PreferenceHelper.initPreferenceHelper(this)
        startKoin {
            androidLogger()
            androidContext(this@AppController)
            modules(listOf(apiModule, databaseModule, viewModelModule, repositoryModule))
        }
    }

    companion object {
        lateinit var instance: AppController
            private set
    }
}