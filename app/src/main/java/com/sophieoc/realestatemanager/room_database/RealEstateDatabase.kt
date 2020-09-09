package com.sophieoc.realestatemanager.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [User::class, Property::class], version = 1)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var instance: RealEstateDatabase? = null
    }

    operator fun invoke(context: Context) = instance?: synchronized(this){
        instance?: createDatabase(context).also { instance = it}
    }

    private fun createDatabase(context: Context): RealEstateDatabase =
            Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateDatabase::class.java,
                    "RealEstate.db"
            )//.fallbackToDestructiveMigration()
               //     .addCallback(roomCallback())
                    .build()


    private fun roomCallback(): Callback? {
        return object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                GlobalScope.launch {

                }
            }
        }
    }
}
