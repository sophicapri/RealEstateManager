package com.sophieoc.realestatemanager.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.presentation.ui.property.PropertyUiState
import com.sophieoc.realestatemanager.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(private val propertySource: PropertyRepository) :
    ViewModel() {
    var property = Property()
    private val _propertyToUpsert: MutableStateFlow<PropertyUiState> =
        MutableStateFlow(PropertyUiState.Loading(Property()))
    val propertySaved: StateFlow<PropertyUiState> = _propertyToUpsert

    fun upsertProperty() {
        property.nbrOfPictures = property.photos.size
        viewModelScope.launch {
            propertySource.upsert(property)
                .catch { e ->
                    _propertyToUpsert.value = PropertyUiState.Error(e)
                }
                .collect {
                    _propertyToUpsert.value = PropertyUiState.Success(property)
                }
        }
    }

    fun getPropertyById(propertyId: String): StateFlow<PropertyUiState> {
        val property: MutableStateFlow<PropertyUiState>
        = MutableStateFlow(PropertyUiState.Loading(Property()))
        viewModelScope.launch {
            propertySource.getPropertyById(propertyId)
                .catch { e ->
                     property.value = PropertyUiState.Error(e)
            }
                .collect { propertyReceived ->
                    property.value = PropertyUiState.Success(propertyReceived)
                }
        }
        return property
    }

    fun getProperties(): Flow<List<Property>> = propertySource.getAllProperties()
}