package com.sophieoc.realestatemanager.presentation.ui.filter

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
    val resultSearch: LiveData<List<Property>> = Transformations.switchMap(_entriesToSearch) { entries ->
        if (_entriesToSearch.value != null) {
            propertySource.getFilteredProperties(
                    propertyType = entries.propertyType, nbrOfBed = entries.nbrOfBed, nbrOfBath = entries.nbrOfBath, nbrOfRooms = entries.nbrOfRoom,
                    propertyAvailability = entries.propertyAvailability, dateOnMarket = entries.dateOnMarket, dateSold = entries.dateSold,
                    priceMin = entries.priceMin, priceMax = entries.priceMax, surfaceMin = entries.surfaceMin, surfaceMax = entries.surfaceMax,
                    nbrOfPictures = entries.nbrOfPictures, area = entries.area)
        }else
            AbsentLiveData.create()
    }

    fun startSearch() {
        _entriesToSearch.value = entries
    }

    fun getPriceOfPriciestProperty() = propertySource.getPriceOfPriciestProperty()

    fun getSurfaceOfBiggestProperty() = propertySource.getSurfaceOfBiggestProperty()

}