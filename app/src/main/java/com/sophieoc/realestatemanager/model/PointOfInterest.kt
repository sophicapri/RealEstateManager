package com.sophieoc.realestatemanager.model

data class PointOfInterest(
        var type: String,
        var name: String,
        var address: String,
        var distance: Int,
        var mainType: String,
) {
    constructor() : this("", "", "", -1, "point of interest")
}