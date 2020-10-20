package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sophieoc.realestatemanager.utils.NO_IMAGE_AVAILABLE_USER

@Entity(tableName = "users")
data class User(
        @PrimaryKey val uid: String,
        @ColumnInfo(name = "username") var username: String,
        @ColumnInfo(name = "email") var email: String,
        @ColumnInfo(name = "url_photo") var urlPhoto: String
) {
    constructor() : this("","", "", NO_IMAGE_AVAILABLE_USER)
}
