package com.sophieoc.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.EntriesFilter
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.utils.AbsentLiveData
import com.sophieoc.realestatemanager.view.activity.MainActivity.Companion.TAG
import java.util.*

class FilterViewModel (private val propertySource: PropertyRepository): ViewModel() {
    var entries = EntriesFilter()
    private val _entriesToSearch: MutableLiveData<EntriesFilter> = MutableLiveData()
    val resultSearch: LiveData<List<Property>> = Transformations.switchMap(_entriesToSearch) {
        if (_entriesToSearch.value != null) {
            Log.d(TAG, ": searrch ok ")
            propertySource.getFilteredProperties(
                    propertyType = it.propertyType, nbrOfBed = it.nbrOfBed, nbrOfBath = it.nbrOfBath, nbrOfRooms = it.nbrOfRoom,
                    propertyAvailability = it.propertyAvailability, dateOnMarket = it.dateOnMarket, dateSold = it.dateSold,
                    priceMin = it.priceMin, priceMax = it.priceMax, surfaceMin = it.surfaceMin, surfaceMax = it.surfaceMax,
                    nbrOfPictures = it.nbrOfPictures, park = it.park,
                    school = it.school, store = it.store, area = it.area)
        }else
            AbsentLiveData.create()
    }

    fun startSearch() {
        Log.d(TAG, "startSearch: ${entries.toString()}")
        _entriesToSearch.value = entries

    }
}