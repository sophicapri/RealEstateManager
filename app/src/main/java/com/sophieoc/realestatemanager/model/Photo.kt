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
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "url_photo") val urlPhoto: String,
        @ColumnInfo(name = "description") var description: String,
        val propertyId: Int
){
    constructor():this(-1, "","",-1)
}