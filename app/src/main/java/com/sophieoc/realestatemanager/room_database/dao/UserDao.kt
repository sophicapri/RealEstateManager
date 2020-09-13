package com.sophieoc.realestatemanager.room_database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties

@Dao
interface UserDao {
    @Insert
    fun insert(user: User): Long

    @Update
    suspend fun update(user: User): Int

    @Transaction
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserWithPropertiesById(uid: String): LiveData<UserWithProperties>

    @Query("DELETE FROM users")
    suspend fun deleteUsers(): Int

    @Query("SELECT * FROM users")
    fun getUsersWithProperties(): LiveData<List<UserWithProperties>>
}