package com.sophieoc.realestatemanager.model.json_to_java

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlacesResult {
    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null

    @SerializedName("next_page_token")
    @Expose
    var nextPageToken: String? = null

    @SerializedName("results")
    @Expose
    var placeDetails: List<PlaceDetails>? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
}