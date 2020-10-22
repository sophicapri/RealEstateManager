package com.sophieoc.realestatemanager.room_database.dao

import androidx.room.*
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    suspend fun upsert(user: User) {
        val id: Long = insert(user)
        if (id == -1L) {
            update(user)
        }
    }

    @Transaction
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserWithPropertiesById(uid: String): UserWithProperties

    @Transaction
    @Query("SELECT * FROM users")
    fun getUsersWithProperties(): List<UserWithProperties>

    @Query("DELETE FROM users")
    suspend fun deleteUsers(): Int
}