package com.sophieoc.realestatemanager.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import com.sophieoc.realestatemanager.view.activity.MainActivity
import java.io.IOException

data class Address (
        @ColumnInfo(name = "street_number") var streetNumber: String,
        @ColumnInfo(name = "street_name") var streetName: String,
        @ColumnInfo(name = "apartment_number") var apartmentNumber: String,
        @ColumnInfo(name = "city") var city: String,
        @ColumnInfo(name = "postal_code") var postalCode: String,
        @ColumnInfo(name = "region") var region: String,
        @ColumnInfo(name = "country") var country: String,
){
    constructor():this( "","","","","","","")

    override fun toString(): String {
        val attributList = arrayOf(streetNumber, streetName, apartmentNumber, city, postalCode, region, country)
        var address = String()
        for (value in attributList){
            if(value.isNotEmpty())
            address += "$value "
        }
        return address.trim()
    }

    fun toLatLng(context: Context): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var latLng: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(this.toString(), 1)
            if (address == null) {
                Log.e(MainActivity.TAG, "getLocationFromAddress: can't find for $this")
            }
            val location = address[0]
            latLng = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return latLng
    }
}