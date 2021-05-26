package com.sophieoc.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sophieoc.realestatemanager.database.dao.PropertyDao
import com.sophieoc.realestatemanager.database.dao.UserDao
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User

@Database(entities = [User::class, Property::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var instance: RealEstateDatabase? = null
        private const val DATABASE_NAME = "RealEstate.db"

        fun getInstance(context: Context): RealEstateDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
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
