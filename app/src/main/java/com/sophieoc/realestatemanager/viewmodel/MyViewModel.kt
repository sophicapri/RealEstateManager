package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository

class MyViewModel(private val userSource: UserRepository, private val propertySource: PropertyRepository): ViewModel() {
    val currentUser = userSource.currentUser

    fun getUserById(uid: String)= userSource.getUserWithProperties(uid)

    fun updateUser(user: User) = userSource.upsertUser(user)

    fun upsertProperty(property: Property): LiveData<Property> = propertySource.upsert(property)

    fun getPropertyById(propertyId: String) = propertySource.getPropertyById(propertyId)

    fun getProperties() = propertySource.getAllProperties()

    fun getPointOfInterests(location: String) = propertySource.getNearbyPointOfInterests(location)
}
