package com.sophieoc.realestatemanager.model

import androidx.room.*
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "property",
        foreignKeys = [ForeignKey(entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"))])
data class Property (
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "type") val type: PropertyType,
        @ColumnInfo(name = "price") var price: String,
        @ColumnInfo(name = "surface") var surface: Int,
        @ColumnInfo(name = "number_of_rooms") var numberOfRooms: Int,
        @ColumnInfo(name = "number_of_bedrooms") var numberOfBedrooms: Int?,
        @ColumnInfo(name = "number_of_bathrooms") var numberOfBathrooms: Int,
        @ColumnInfo(name = "description") var description: String?,
        @ColumnInfo(name = "availability") val availability: PropertyAvailability,
        @ColumnInfo(name = "date_on_market") val dateOnMarket: Date,
        @ColumnInfo(name = "date_sold") val dateSold: Date?,
        @Embedded var address: Address,
        @ColumnInfo(name = "photos")var photos: ArrayList<Photo>,
        @ColumnInfo(name = "point_of_interest")var pointOfInterests: ArrayList<PointOfInterest>,
        @ColumnInfo(name = "user") var user: User
        //var userId: Int
){
    constructor():this(-1, PropertyType.FLAT,"",-1,-1,
            -1,-1,"", PropertyAvailability.AVAILABLE,Date(), Date(),
            Address(), ArrayList(), ArrayList(), User())
}