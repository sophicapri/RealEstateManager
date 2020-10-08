package com.sophieoc.realestatemanager.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.room_database.dao.UserDao

@Database(entities = [User::class, Property::class], version = 8, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun propertyDao(): PropertyDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: RealEstateDatabase? = null
        const val DATABASE_NAME = "RealEstate.db"

        fun getInstance(context: Context): RealEstateDatabase {
            return instance ?: synchronized(this) {
                instance
                        ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RealEstateDatabase =
                Room.databaseBuilder(
                        context.applicationContext,
                        RealEstateDatabase::class.java,
                        DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
    }
}
