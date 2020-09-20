package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photo",
        foreignKeys = [ForeignKey(entity = Property::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("propertyId"))])
data class Photo(
        @PrimaryKey(autoGenerate = true) val id: Int = -1,
        @ColumnInfo(name = "url_photo") val urlPhoto: String,
        @ColumnInfo(name = "description") var description: String,
        val propertyId: String
){
    constructor():this(-1, "https://cdn.pixabay.com/photo/2016/11/18/17/46/architecture-1836070_1280.jpg","","")
}