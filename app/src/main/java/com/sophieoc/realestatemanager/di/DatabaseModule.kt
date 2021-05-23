package com.sophieoc.realestatemanager.di

import android.content.Context
import com.sophieoc.realestatemanager.room_database.RealEstateDatabase
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RealEstateDatabase {
        return RealEstateDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideUserDao(database: RealEstateDatabase): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun providePropertyDao(database: RealEstateDatabase): PropertyDao {
        return database.propertyDao()
    }
}