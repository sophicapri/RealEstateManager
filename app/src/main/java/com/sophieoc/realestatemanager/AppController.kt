package com.sophieoc.realestatemanager

import android.app.Application
import com.sophieoc.realestatemanager.room_database.RealEstateDatabase.Companion.DATABASE_NAME
import com.sophieoc.realestatemanager.utils.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppController: Application() {
    override fun onCreate() {
        super.onCreate()
        deleteDatabase(DATABASE_NAME)
        PreferenceHelper.initPreferenceHelper(this)
        startKoin {
            androidLogger()
            androidContext(this@AppController)
            modules(listOf(apiModule, databaseModule, viewModelModule, repositoryModule))
        }
    }
}