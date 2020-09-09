package com.sophieoc.realestatemanager.room_database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType

class Converters {

    @TypeConverter
    fun listPhotoToJson(value: List<Photo>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToListPhoto(value: String) = Gson().fromJson(value, Array<Photo>::class.java).toList()

    @TypeConverter
    fun listPointOfInterestToJson(value: List<PointOfInterest>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToListPointOfInterest(value: String) = Gson().fromJson(value, Array<PointOfInterest>::class.java).toList()

    @TypeConverter
    fun userToJson(value: User?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToUser(value: String) = Gson().fromJson(value, User::class.java)

    @TypeConverter
    fun enumToJson(value: PropertyType?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToEnumPropertyType(value: String) = Gson().fromJson(value, PropertyType::class.java)

    @TypeConverter
    fun enumToJson(value: PropertyAvailability?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToEnumPropertyAvailability(value: String) = Gson().fromJson(value, PropertyAvailability::class.java)
}