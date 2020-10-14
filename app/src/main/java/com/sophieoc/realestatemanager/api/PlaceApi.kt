package com.sophieoc.realestatemanager.api

import com.sophieoc.realestatemanager.model.json_to_java.PlacesResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceApi {

    @GET("nearbysearch/json?rankby=prominence&type=park&radius=2000")
    suspend fun getNearbyParks(@Query("location") location: String?): PlacesResult

    @GET("nearbysearch/json?rankby=prominence&type=store&radius=2000")
    suspend fun getNearbyStores(@Query("location") location: String?): PlacesResult

    @GET("nearbysearch/json?rankby=prominence&type=school&radius=2000")
    suspend fun getNearbySchools(@Query("location") location: String?): PlacesResult

}