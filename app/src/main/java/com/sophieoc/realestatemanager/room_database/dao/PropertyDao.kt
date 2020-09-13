package com.sophieoc.realestatemanager.room_database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sophieoc.realestatemanager.model.Property

@Dao
interface PropertyDao {

    @Insert()
    suspend fun insert(property: Property): Long

    @Update
    suspend fun update(property: Property): Int

    @Query("SELECT * FROM property")
    fun getProperties(): LiveData<List<Property>>

    @Query("SELECT * FROM property WHERE id = :id")
    fun getPropertyById(id: String): LiveData<Property>

    /*@Query("""SELECT * FROM property WHERE type = :type AND surface > :minSurface
        AND surface < :maxSurface AND date_on_market < :dateOnMarket AND date_sold < :dateSold 
        AND  """)
    suspend fun getPropertiesFilteredBy(type: PropertyType,
                                        minSurface: Int = 15, maxSurface: Int = 500,
                                        dateOnMarket : Date, dateSold: Date, area)
     */
}