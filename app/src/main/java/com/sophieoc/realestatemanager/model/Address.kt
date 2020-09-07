package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class Address (
        @ColumnInfo(name = "street_number") var streetNumber: String,
        @ColumnInfo(name = "street_name") var streetName: String,
        @ColumnInfo(name = "apartment_number") var apartmentNumber: String?,
        @ColumnInfo(name = "city") var city: String,
        @ColumnInfo(name = "postal_code") var postalCode: String,
        @ColumnInfo(name = "region") var region: String?,
        @ColumnInfo(name = "country") var country: String,
){
    constructor():this( "","","","","","","")

    fun toLatLng(){
        //
    }
}