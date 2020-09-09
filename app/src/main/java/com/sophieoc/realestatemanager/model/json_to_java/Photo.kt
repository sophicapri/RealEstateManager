package com.sophieoc.realestatemanager.model.json_to_java

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Photo {
    @SerializedName("height")
    @Expose
    var height: Int? = null

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<String>? = null

    @SerializedName("photo_reference")
    @Expose
    var photoReference: String? = null

    @SerializedName("width")
    @Expose
    var width: Int? = null
}