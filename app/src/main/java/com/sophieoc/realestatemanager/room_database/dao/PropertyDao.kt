package com.sophieoc.realestatemanager.room_database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.sophieoc.realestatemanager.model.Property
import java.util.*


@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(property: Property): Long

    @Update
    suspend fun update(property: Property): Int

    suspend fun upsert(property: Property): Long {
        val id: Long = insert(property)
        return if (id == -1L) {
            update(property).toLong()
        } else
            id
    }

    @Query("SELECT * FROM property")
    fun getProperties(): List<Property>

    @Query("SELECT * FROM property")
    fun getPropertiesWithCursor(): Cursor

    @Query("SELECT * FROM property WHERE user_id = :userId")
    fun getPropertiesWithCursorForUser(userId: String): Cursor

    @Query("SELECT * FROM property WHERE id = :id")
    fun getPropertyById(id: String): LiveData<Property>

    @Query("DELETE FROM property WHERE id = :propertyId")
    fun deleteById(propertyId: String): Int

    @Query("DELETE FROM property")
    fun deleteAll(): Int

    @Query("""SELECT * FROM property WHERE  
        CASE WHEN :propertyType IS NOT NULL THEN property.type = :propertyType ELSE 1 END 
        AND CASE WHEN :nbrOfBed IS NOT NULL THEN property.number_of_bedrooms = :nbrOfBed ELSE 1 END
        AND CASE WHEN :nbrOfBath IS NOT NULL THEN property.number_of_bathrooms = :nbrOfBath ELSE 1 END
        AND CASE WHEN :propertyAvailability IS NOT NULL THEN property.availability = :propertyAvailability ELSE 1 END 
        AND CASE WHEN :dateOnMarket IS NOT NULL THEN property.date_on_market = :dateOnMarket ELSE 1 END
        AND CASE WHEN :dateSold IS NOT NULL THEN property.date_sold = :dateSold ELSE 1 END
        AND property.price > :priceMin AND property.price < :priceMax
        AND property.surface > :surfaceMin AND property.surface < :surfaceMax 
        AND CASE WHEN :pointOfInterests IS NOT NULL THEN property.point_of_interest = :pointOfInterests ELSE 1 END
        AND CASE WHEN :nbrOfPictures IS NOT NULL THEN number_of_pictures >= :nbrOfPictures ELSE 1 END""")
    fun getFilteredList(
            propertyType: String?, nbrOfBed: Int?, nbrOfBath: Int?,
            propertyAvailability: String?, dateOnMarket: Date?, dateSold: Date?,
            priceMin: Int, priceMax: Int?, surfaceMin: Int, surfaceMax: Int?,
            pointOfInterests: String?, nbrOfPictures: Int?): List<Property>
}