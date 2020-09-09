package com.sophieoc.realestatemanager.model.json_to_java

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class OpeningHours {
    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null

    @SerializedName("periods")
    @Expose
    var periods: List<Period>? = null

    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<String>? = null
}