package com.sophieoc.realestatemanager.presentation.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophieoc.realestatemanager.model.EntriesFilter
import com.sophieoc.realestatemanager.presentation.ui.propertylist.PropertyListUiState
import com.sophieoc.realestatemanager.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(private val propertySource: PropertyRepository): ViewModel() {
    var entries = EntriesFilter()
    private val _resultSearch : MutableStateFlow<PropertyListUiState>
    = MutableStateFlow(PropertyListUiState.Loading(emptyList()))
    val resultSearch: StateFlow<PropertyListUiState> = _resultSearch

    fun startSearch() {
        viewModelScope.launch {
            propertySource.getFilteredProperties(
                propertyType = entries.propertyType, nbrOfBed = entries.nbrOfBed, nbrOfBath = entries.nbrOfBath, nbrOfRooms = entries.nbrOfRoom,
                propertyAvailability = entries.propertyAvailability, dateOnMarket = entries.dateOnMarket, dateSold = entries.dateSold,
                priceMin = entries.priceMin, priceMax = entries.priceMax, surfaceMin = entries.surfaceMin, surfaceMax = entries.surfaceMax,
                nbrOfPictures = entries.nbrOfPictures, area = entries.area)
                .catch { e ->
                    _resultSearch.value = PropertyListUiState.Error(e)
                }
                .collect { propertyList ->
                    _resultSearch.value = PropertyListUiState.Success(propertyList)
                }
        }
    }

    fun getPriceOfPriciestProperty() = propertySource.getPriceOfPriciestProperty()

    fun getSurfaceOfBiggestProperty() = propertySource.getSurfaceOfBiggestProperty()

}