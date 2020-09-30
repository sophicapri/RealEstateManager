package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class PointOfInterest(
        var type: String,
        var name: String,
        var address: String,
        var distance: Int,
) {
    constructor() : this("", "", "", -1)
}