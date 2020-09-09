package com.sophieoc.realestatemanager.room_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.google.j2objc.annotations.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserWithPropertiesById(uid: Int): UserWithProperties
}