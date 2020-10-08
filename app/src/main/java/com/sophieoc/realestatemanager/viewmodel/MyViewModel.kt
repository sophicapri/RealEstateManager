package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository
import java.util.*

class MyViewModel(private val userSource: UserRepository, private val propertySource: PropertyRepository) : ViewModel() {
    val currentUser = userSource.currentUser

    fun getUserById(uid: String): LiveData<UserWithProperties> = userSource.getUserWithProperties(uid)

    fun updateUser(user: User) = userSource.upsertUser(user)

    fun upsertProperty(property: Property): LiveData<Property> = propertySource.upsert(property)

    fun getPropertyById(propertyId: String): LiveData<Property> = propertySource.getPropertyById(propertyId)

    fun getProperties(): LiveData<List<Property>> = propertySource.getAllProperties()

    fun getPointOfInterests(location: String): LiveData<List<PlaceDetails>> = propertySource.getNearbyPointOfInterests(location)

    fun getFilteredList(
            propertyType: String?, nbrOfBed: Int?, nbrOfBath: Int?, propertyAvailability: String?,
            dateOnMarket: Date?, dateSold: Date?, priceMin: Int, priceMax: Int,
            surfaceMin: Int, surfaceMax: Int, pointOfInterests: String?,
    ): LiveData<List<Property>> {
        return propertySource.getFilteredProperties(propertyType, nbrOfBed, nbrOfBath, propertyAvailability,
                dateOnMarket, dateSold, priceMin, priceMax, surfaceMin, surfaceMax, pointOfInterests)
    }
}
