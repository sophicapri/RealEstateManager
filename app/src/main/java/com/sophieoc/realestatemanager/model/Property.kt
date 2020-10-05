package com.sophieoc.realestatemanager.model

import android.content.ContentValues
import android.util.Log
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
        @PrimaryKey var id: String,
        @ColumnInfo(name = "type") var type: PropertyType,
        @ColumnInfo(name = "price") var price: Int,
        @ColumnInfo(name = "surface") var surface: Int,
        @ColumnInfo(name = "number_of_bedrooms") var numberOfBedrooms: Int?,
        @ColumnInfo(name = "number_of_bathrooms") var numberOfBathrooms: Int,
        @ColumnInfo(name = "description") var description: String?,
        @ColumnInfo(name = "availability") var availability: PropertyAvailability,
        @ColumnInfo(name = "date_on_market") var dateOnMarket: Date?,
        @ColumnInfo(name = "date_sold") var dateSold: Date?,
        @Embedded var address: Address,
        @ColumnInfo(name = "photos") var photos: List<Photo>,
        @ColumnInfo(name = "point_of_interest") var pointOfInterests: List<PointOfInterest>,
        @ColumnInfo(name = "user_id") var userId: String,
) {
    constructor() : this(UUID.randomUUID().toString(), PropertyType.FLAT, -1, -1,
            -1, -1, "", PropertyAvailability.AVAILABLE, null, null,
            Address(), ArrayList<Photo>(), ArrayList<PointOfInterest>(), "")

    companion object {
        fun fromContentValues(values: ContentValues): Property {
            val property = Property()
            if (values.containsKey("id")) property.id = values.getAsLong("id").toString()
            if (values.containsKey("type")) property.type = Gson().fromJson(values.getAsString("type"), PropertyType::class.java)
            if (values.containsKey("price")) property.price = values.getAsInteger("price")
            if (values.containsKey("surface")) property.surface = values.getAsInteger("surface")
            if (values.containsKey("numberOfBedrooms")) property.numberOfBedrooms = values.getAsInteger("numberOfBedrooms")
            if (values.containsKey("numberOfBathrooms")) property.numberOfBathrooms = values.getAsInteger("numberOfBathrooms")
            if (values.containsKey("description")) property.description = values.getAsString("description")
            if (values.containsKey("availability")) property.availability = Gson().fromJson(
                    values.getAsString("availability"), PropertyAvailability::class.java)
            if (values.containsKey("dateOnMarket")) property.dateOnMarket = Date(values.getAsLong("dateOnMarket"))
            if (values.containsKey("dateSold")) property.dateSold =  Date(values.getAsLong("dateSold"))
            // address fields
            if (values.containsKey("streetNumber")) property.address.streetNumber = values.getAsString("streetNumber")
            if (values.containsKey("streetName")) property.address.streetName = values.getAsString("streetName")
            if (values.containsKey("apartmentNumber")) property.address.apartmentNumber = values.getAsString("apartmentNumber")
            if (values.containsKey("city")) property.address.city = values.getAsString("city")
            if (values.containsKey("postalCode")) property.address.postalCode = values.getAsString("postalCode")
            if (values.containsKey("region")) property.address.region = values.getAsString("region")
            if (values.containsKey("country")) property.address.country = values.getAsString("country")
            //
            if (values.containsKey("photos")) property.photos = Gson().fromJson(
                    values.getAsString("photos"), Array<Photo>::class.java).toList()
            if (values.containsKey("pointOfInterests")) property.pointOfInterests = Gson().fromJson(
                    values.getAsString("pointOfInterests"), Array<PointOfInterest>::class.java).toList()
            if (values.containsKey("userId")) property.userId = values.getAsString("userId")

            Log.d("TESTS:", "fromContentValues: $property")
            return property
        }
    }

    override fun toString(): String {
        return "id = $id / type = $type / price = $price / surface = $surface / beds = $numberOfBedrooms" +
                " / baths = $numberOfBathrooms / description = $description / " +
                "availability = ${availability.s} / " +
                "address = ${address.toString()} / " +
                "nbr of photos = ${photos.size}" +
                "user_id = $userId"
    }
}