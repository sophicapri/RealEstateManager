package com.sophieoc.realestatemanager.di

import com.sophieoc.realestatemanager.api.PlaceService
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideUserRepository(userDao : UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Singleton
    @Provides
    fun providePropertyRepository(propertyDao: PropertyDao, placeService: PlaceService): PropertyRepository {
        return PropertyRepository(propertyDao, placeService)
    }
}