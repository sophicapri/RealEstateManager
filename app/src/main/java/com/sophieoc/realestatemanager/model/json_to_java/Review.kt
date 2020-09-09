package com.sophieoc.realestatemanager.model.json_to_java

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Review {
    @SerializedName("author_name")
    @Expose
    var authorName: String? = null

    @SerializedName("author_url")
    @Expose
    var authorUrl: String? = null

    @SerializedName("language")
    @Expose
    var language: String? = null

    @SerializedName("profile_photo_url")
    @Expose
    var profilePhotoUrl: String? = null

    @SerializedName("rating")
    @Expose
    var rating: Int? = null

    @SerializedName("relative_time_description")
    @Expose
    var relativeTimeDescription: String? = null

    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("time")
    @Expose
    var time: Int? = null
}