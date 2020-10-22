package com.sophieoc.realestatemanager

import android.app.Application
import com.sophieoc.realestatemanager.utils.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppController: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        PreferenceHelper.initPreferenceHelper(this)
        //deleteDatabase(RealEstateDatabase.DATABASE_NAME)
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