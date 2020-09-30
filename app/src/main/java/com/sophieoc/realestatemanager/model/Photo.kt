package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class Photo(
        @ColumnInfo(name = "url_photo") val urlPhoto: String,
        @ColumnInfo(name = "description") var description: String,
){
    constructor():this( "https://cdn.pixabay.com/photo/2016/11/18/17/46/architecture-1836070_1280.jpg","")
}