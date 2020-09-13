package com.sophieoc.realestatemanager.room_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sophieoc.realestatemanager.model.Property


@Dao
interface PropertyDao {

    @Insert()
    suspend fun insert(property: Property)

    @Query("SELECT * FROM property")
    suspend fun getProperties(): List<Property>?

    /*@Query("""SELECT * FROM property WHERE type = :type AND surface > :minSurface
        AND surface < :maxSurface AND date_on_market < :dateOnMarket AND date_sold < :dateSold 
        AND  """)
    suspend fun getPropertiesFilteredBy(type: PropertyType,
                                        minSurface: Int = 15, maxSurface: Int = 500,
                                        dateOnMarket : Date, dateSold: Date, area)

     */
}