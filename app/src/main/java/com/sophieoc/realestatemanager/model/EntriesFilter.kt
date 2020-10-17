package com.sophieoc.realestatemanager.model

import java.util.*

data class EntriesFilter (
     var propertyType: String? = null,
     var nbrOfBed: Int? = null,
     var nbrOfBath: Int? = null,
     var nbrOfRoom: Int? = null,
     var propertyAvailability: String? = null,
     var dateOnMarket: Date? = null,
     var dateSold: Date? = null,
     var priceMin: Int = 0,
     var priceMax: Int = 100000000,
     var surfaceMin: Int = 0,
     var surfaceMax: Int = 1000,
     var nbrOfPictures: Int? = null,
     var park: String? = null,
     var school: String? = null,
     var store: String? = null,
     var area: String? = null,
){
    override fun toString(): String {
        return "type = $propertyType  | bed = $nbrOfBed | bath = $nbrOfBath | room = $nbrOfRoom" +
                "| availability = $propertyAvailability | onMarket = $dateOnMarket | sold = $dateSold" +
                "| priceMin = $priceMin | priceMax = $priceMax | surfaceMin = $surfaceMin |" +
                "surfaceMax = $surfaceMax | pics = $nbrOfPictures | park = $park | " +
                "school = $school | store = $store | area = $area"
    }

}