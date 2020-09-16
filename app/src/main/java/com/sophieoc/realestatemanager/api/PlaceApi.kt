package com.sophieoc.realestatemanager.api

import com.sophieoc.realestatemanager.model.json_to_java.PlacesResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceApi {

    @GET("nearbysearch/json?rankby=distance")
    suspend fun getNearbyPlaces(@Path("location") location: String?): PlacesResult

}