package com.sophieoc.realestatemanager.database.dao

import androidx.room.*
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import kotlinx.coroutines.flow.Flow

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
    fun getUserWithPropertiesById(uid: String): Flow<UserWithProperties>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUsersWithProperties(): Flow<List<UserWithProperties>>

    @Query("DELETE FROM users")
    suspend fun deleteUsers(): Int
}