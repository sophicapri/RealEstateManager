package com.sophieoc.realestatemanager.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

data class Address(
        @ColumnInfo(name = "street_number") var streetNumber: String,
        @ColumnInfo(name = "street_name") var streetName: String,
        @ColumnInfo(name = "apartment_number") var apartmentNumber: String,
        @ColumnInfo(name = "city") var city: String,
        @ColumnInfo(name = "postal_code") var postalCode: String,
        @ColumnInfo(name = "region") var region: String,
        @ColumnInfo(name = "country") var country: String,
) {
    constructor() : this("", "", "", "", "", "", "United States")

    override fun toString(): String {
        val attributList = arrayOf(streetNumber, apartmentNumber, streetName, city, postalCode, region, country)
        var address = String()

        for (value in attributList) {
            if (value.isNotEmpty())
                address += "$value "
        }
        return address
    }

    fun toLatLng(context: Context): LatLng {
        val coder = Geocoder(context)
        var address: List<Address>? = null
        val latLng: LatLng
        try {
            address = coder.getFromLocationName(this.toString(), 1)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        latLng = if (address == null || address.isEmpty()) {
            LatLng(0.0, 0.0)
        } else {
            val location = address[0]
            LatLng(location.latitude, location.longitude)
        }
        return latLng
    }

    companion object {
        const val STREET_NAME_POSITION = 2
        const val POSTAL_CODE_POSITION = 4
    }
}