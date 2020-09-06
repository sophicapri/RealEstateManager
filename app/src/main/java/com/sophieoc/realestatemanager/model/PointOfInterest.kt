package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "point_of_interest",
        foreignKeys = [ForeignKey(entity = Property::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("propertyId"))])
data class PointOfInterest(
        @PrimaryKey(autoGenerate = true) val uid: Int,
        @ColumnInfo(name = "type") val type: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "distance") var distance: Int,
        val propertyId: Int
){
    constructor():this(-1, "","","",-1,-1)
}