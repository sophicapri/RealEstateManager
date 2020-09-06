package com.sophieoc.realestatemanager.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.room_database.dao.UserDao

@Database(entities = [User::class, Property::class], version = 1)
abstract class RealEstateDatabase : RoomDatabase() {
    companion object {
        private val INSTANCE: RealEstateDatabase? = null
    }

    abstract fun userDao(): UserDao
    abstract fun propertyDao(): PropertyDao

    fun getInstance(context: Context): RealEstateDatabase {
        return INSTANCE
                ?: Room.databaseBuilder(
                        context.applicationContext,
                        RealEstateDatabase::class.java, "RealEstateDatabase").build()
    }
}
