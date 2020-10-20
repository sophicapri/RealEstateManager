package com.sophieoc.realestatemanager.model

import androidx.room.ColumnInfo
import com.sophieoc.realestatemanager.utils.NO_IMAGE_AVAILABLE_PROPERTY

data class Photo(
        @ColumnInfo(name = "url_photo") val urlPhoto: String,
        @ColumnInfo(name = "description") var description: String,
) {
    constructor() : this(NO_IMAGE_AVAILABLE_PROPERTY, "")
}
