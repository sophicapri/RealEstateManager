package com.sophieoc.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation

data class PropertyAndPhotos (
        @Embedded
        val property: Property,
        @Relation(
                parentColumn = "id",
                entityColumn = "propertyId"
        ) val photos: List<Photo>,
        var pointOfInterests: List<PointOfInterest>
)