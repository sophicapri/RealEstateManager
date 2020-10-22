package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.utils.AbsentLiveData

class PropertyViewModel(private val propertySource: PropertyRepository) : ViewModel() {
    var property = Property()
    private val _propertyToUpsert: MutableLiveData<Property> = MutableLiveData()
    val propertySaved: LiveData<Property> = Transformations.switchMap(_propertyToUpsert) {
        if (_propertyToUpsert.value != null)
            propertySource.upsert(it)
        else
            AbsentLiveData.create()
    }

    fun upsertProperty() {
        property.nbrOfPictures = property.photos.size
        _propertyToUpsert.value = property
    }

    fun getPointOfInterests(location: String): LiveData<List<PlaceDetails>> = propertySource.getNearbyPointOfInterests(location)

    fun getPropertyById(propertyId: String): LiveData<Property> = propertySource.getPropertyById(propertyId)

    fun getProperties(): LiveData<List<Property>> = propertySource.getAllProperties()
}