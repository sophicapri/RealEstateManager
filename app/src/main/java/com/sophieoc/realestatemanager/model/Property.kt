package com.sophieoc.realestatemanager.model

import androidx.room.*
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "property",
        foreignKeys = [ForeignKey(entity = User::class,
                parentColumns = arrayOf("uid"),
                childColumns = arrayOf("user_id"))],
        indices = [Index(value = ["user_id"])])
data class Property(
        @PrimaryKey val id: String,
        @ColumnInfo(name = "type") var type: PropertyType,
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
        @ColumnInfo(name = "photos") var photos: List<Photo>,
        @ColumnInfo(name = "point_of_interest") var pointOfInterests: List<PointOfInterest>,
        @ColumnInfo(name = "user_id") var userId: String,
){
    constructor() : this(UUID.randomUUID().toString(), PropertyType.FLAT, "", -1, -1,
            -1, -1, "", PropertyAvailability.AVAILABLE, Date(), Date(),
            Address(), ArrayList<Photo>(), ArrayList<PointOfInterest>(), "")
}