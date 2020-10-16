package com.sophieoc.realestatemanager.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.utils.NO_IMAGE_AVAILABLE

data class Photo(
        @ColumnInfo(name = "url_photo") val urlPhoto: String,
        @ColumnInfo(name = "description") var description: String,
) {
    constructor() : this(NO_IMAGE_AVAILABLE, "")
}
