package com.sophieoc.realestatemanager.room_database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.sophieoc.realestatemanager.model.Property


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

    @RawQuery(observedEntities = [Property::class])
    fun getFilteredList(query: SupportSQLiteQuery): List<Property>
}