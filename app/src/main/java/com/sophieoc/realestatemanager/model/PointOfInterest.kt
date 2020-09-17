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
        @ColumnInfo(name = "type") var type: String,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "distance") var distance: Int,
        var propertyId: String
){
    constructor():this( "","","",-1,"")
}