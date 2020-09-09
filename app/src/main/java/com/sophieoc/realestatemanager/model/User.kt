package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true) val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String,
        @ColumnInfo(name = "email") var email: String,
        @ColumnInfo(name = "password") var password: String,
        @ColumnInfo(name = "url_photo") var urlPhoto: String
) {
    constructor() : this(-1, "","", "", "", "todo : add default pic link")
}
