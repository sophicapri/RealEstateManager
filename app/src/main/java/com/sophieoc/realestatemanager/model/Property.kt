package com.sophieoc.realestatemanager.model

import android.content.ContentValues
import androidx.room.*
import com.google.gson.Gson
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType
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
        //@ColumnInfo(name = "number_of_rooms") var numberOfRooms: Int,
        @ColumnInfo(name = "number_of_bedrooms") var numberOfBedrooms: Int?,
        @ColumnInfo(name = "number_of_bathrooms") var numberOfBathrooms: Int,
        @ColumnInfo(name = "description") var description: String?,
        @ColumnInfo(name = "availability") var availability: PropertyAvailability,
        @ColumnInfo(name = "date_on_market") var dateOnMarket: Date,
        @ColumnInfo(name = "date_sold") var dateSold: Date?,
        @Embedded var address: Address,
        @ColumnInfo(name = "photos") var photos: List<Photo>,
        @ColumnInfo(name = "point_of_interest") var pointOfInterests: List<PointOfInterest>,
        @ColumnInfo(name = "user_id") var userId: String,
) {
    constructor() : this(UUID.randomUUID().toString(), PropertyType.FLAT, "", -1,
            -1, -1, "", PropertyAvailability.AVAILABLE, Date(), Date(),
            Address(), ArrayList<Photo>(), ArrayList<PointOfInterest>(), "")

    companion object {
        fun fromContentValues(values: ContentValues): Property {
            val property = Property()
            //TODO : ADD ID
            if (values.containsKey("type")) property.type = Gson().fromJson(values.getAsString("type"), PropertyType::class.java)
            if (values.containsKey("price")) property.price = values.getAsString("price")
            if (values.containsKey("surface")) property.surface = values.getAsInteger("surface")
            if (values.containsKey("numberOfBedrooms")) property.numberOfBedrooms = values.getAsInteger("numberOfBedrooms")
            if (values.containsKey("numberOfBathrooms")) property.numberOfBathrooms = values.getAsInteger("numberOfBathrooms")
            if (values.containsKey("description")) property.description = values.getAsString("description")
            if (values.containsKey("availability")) property.availability = Gson().fromJson(values.getAsString("type"), PropertyAvailability::class.java)
            if (values.containsKey("dateOnMarket")) property.dateOnMarket = Date(values.getAsLong("dateOnMarket"))
            if (values.containsKey("dateSold")) property.dateSold =  Date(values.getAsLong("dateSold"))


            if (values.containsKey("description")) property.description = values.getAsString("description")
            if (values.containsKey("description")) property.description = values.getAsString("description")
            if (values.containsKey("description")) property.description = values.getAsString("description")
            if (values.containsKey("description")) property.description = values.getAsString("description")
            if (values.containsKey("description")) property.description = values.getAsString("description")

            return property
        }
    }
}