package com.sophieoc.realestatemanager.utils

import android.app.Application
import androidx.room.Room
import com.sophieoc.realestatemanager.api.PlaceApi
import com.sophieoc.realestatemanager.api.PlaceService
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository
import com.sophieoc.realestatemanager.room_database.RealEstateDatabase
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import com.sophieoc.realestatemanager.viewmodel.FilterViewModel
import com.sophieoc.realestatemanager.viewmodel.MyViewModel
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val apiModule = module {

    fun providePlaceApi(placeService: PlaceService): PlaceApi {
        return placeService.createService(PlaceApi::class.java)
    }
    single { providePlaceApi(get()) }
    single { PlaceService }

}

val databaseModule = module {

    fun provideDatabase(application: Application): RealEstateDatabase {
        return RealEstateDatabase.getInstance(application)
    }

    fun provideUserDao(database: RealEstateDatabase): UserDao {
        return database.userDao()
    }

    fun providePropertyDao(database: RealEstateDatabase): PropertyDao {
        return database.propertyDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideUserDao(get()) }
    single { providePropertyDao(get()) }
}

val repositoryModule = module {

    fun provideUserRepository(userDao : UserDao): UserRepository {
        return UserRepository(userDao)
    }

    fun providePropertyRepository(propertyDao: PropertyDao, placeApi: PlaceApi): PropertyRepository {
        return PropertyRepository(propertyDao, placeApi)
    }
    single { provideUserRepository(get()) }
    single { providePropertyRepository(get(), get()) }

}

val viewModelModule = module {
    // Specific viewModel pattern to tell Koin how to build ViewModel
    viewModel {
        MyViewModel(get(), get())
    }

    viewModel {
        PropertyViewModel(get())
    }

    viewModel {
        FilterViewModel(get())
    }
}