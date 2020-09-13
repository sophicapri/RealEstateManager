package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey val uid: String,
        @ColumnInfo(name = "username") val username: String,
        @ColumnInfo(name = "email") var email: String,
        @ColumnInfo(name = "url_photo") var urlPhoto: String?
) {
    constructor() : this("idDefault","", "", "todo : add default pic link")
}
