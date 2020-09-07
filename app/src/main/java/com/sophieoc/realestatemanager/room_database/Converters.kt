package com.sophieoc.realestatemanager.room_database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.model.PointOfInterest

class Converters {

    @TypeConverter
    fun listPhotoToJson(value: List<Photo>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToListPhoto(value: String) = Gson().fromJson(value, Array<Photo>::class.java).toList()

    @TypeConverter
    fun listPointOfInterestToJson(value: List<PointOfInterest>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToListPointOfInterest(value: String) = Gson().fromJson(value, Array<PointOfInterest>::class.java).toList()

}