package com.sophieoc.realestatemanager.model.json_to_java

import android.location.Location
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlaceDetails {
    @SerializedName("address_components")
    @Expose
    var addressComponents: List<AddressComponent>? = null

    @SerializedName("adr_address")
    @Expose
    var adrAddress: String? = null

    @SerializedName("business_status")
    @Expose
    var businessStatus: String? = null

    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null

    @SerializedName("formatted_phone_number")
    @Expose
    var formattedPhoneNumber: String? = null

    // --- GETTERS ---
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null

    // --- SETTERS ---
    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("international_phone_number")
    @Expose
    var internationalPhoneNumber: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("opening_hours")
    @Expose
    var openingHours: OpeningHours? = null

    @SerializedName("photos")
    @Expose
    var photos: List<Photo>? = null

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null

    @SerializedName("plus_code")
    @Expose
    var plusCode: PlusCode? = null

    @SerializedName("rating")
    @Expose
    var rating: Double? = null

    @SerializedName("reference")
    @Expose
    var reference: String? = null

    @SerializedName("reviews")
    @Expose
    var reviews: List<Review>? = null

    @SerializedName("scope")
    @Expose
    var scope: String? = null

    @SerializedName("types")
    @Expose
    var types: List<String>? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("user_ratings_total")
    @Expose
    val userRatingsTotal: Int? = null

    @SerializedName("utc_offset")
    @Expose
    var utcOffset: Int? = null

    @SerializedName("vicinity")
    @Expose
    var vicinity: String? = null

    @SerializedName("website")
    @Expose
    var website: String? = null

    fun getDistanceFrom(propertyLocation: Location?): Int {
        val location = Location(name)
        geometry?.location?.lat?.let {  location.latitude = it }
        geometry?.location?.lng?.let { location.longitude = it }
        return location.distanceTo(propertyLocation).toInt()
    }
}