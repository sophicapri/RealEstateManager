package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.EntriesFilter
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.utils.AbsentLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(private val propertySource: PropertyRepository): ViewModel() {
    var entries = EntriesFilter()
    private val _entriesToSearch: MutableLiveData<EntriesFilter> = MutableLiveData()
    val resultSearch: LiveData<List<Property>> = Transformations.switchMap(_entriesToSearch) {
        if (_entriesToSearch.value != null) {
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
        _entriesToSearch.value = entries
    }

    fun getPriceOfPriciestProperty() = propertySource.getPriceOfPriciestProperty()

    fun getSurfaceOfBiggestProperty() = propertySource.getSurfaceOfBiggestProperty()

}