package com.sophieoc.realestatemanager.room_database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sophieoc.realestatemanager.model.Property


@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg property: Property)

    @Update
    suspend fun update(property: Property)

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