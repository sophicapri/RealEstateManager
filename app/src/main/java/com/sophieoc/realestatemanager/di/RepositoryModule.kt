package com.sophieoc.realestatemanager.di

import com.sophieoc.realestatemanager.api.PlaceService
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository
import com.sophieoc.realestatemanager.database.dao.PropertyDao
import com.sophieoc.realestatemanager.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideUserRepository(userDao : UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Singleton
    @Provides
    fun providePropertyRepository(propertyDao: PropertyDao): PropertyRepository {
        return PropertyRepository(propertyDao)
    }
}