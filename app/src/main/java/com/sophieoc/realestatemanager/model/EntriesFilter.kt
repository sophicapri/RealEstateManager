package com.sophieoc.realestatemanager.model

import java.util.*

data class EntriesFilter(
        var propertyType: String? = null,
        var nbrOfBed: Int? = null,
        var nbrOfBath: Int? = null,
        var nbrOfRoom: Int? = null,
        var propertyAvailability: String? = null,
        var dateOnMarket: Date? = null,
        var dateSold: Date? = null,
        var priceMin: Int? = null,
        var priceMax: Int? = null,
        var surfaceMin: Int? = null,
        var surfaceMax: Int? = null,
        var nbrOfPictures: Int? = null,
        var area: String? = null,
) {
    override fun toString(): String {
        return "type = $propertyType  | bed = $nbrOfBed | bath = $nbrOfBath | room = $nbrOfRoom" +
                "| availability = $propertyAvailability | onMarket = $dateOnMarket | sold = $dateSold" +
                "| priceMin = $priceMin | priceMax = $priceMax | surfaceMin = $surfaceMin |" +
                "surfaceMax = $surfaceMax | pics = $nbrOfPictures | area = $area"
    }
}