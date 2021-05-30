package com.sophieoc.realestatemanager.ui.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.ui.propertylist.PropertyListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(private val propertySource: PropertyRepository) :
    ViewModel() {
    var property = Property()
    private val _propertyToUpsert: MutableStateFlow<PropertyUiState> =
        MutableStateFlow(PropertyUiState.Loading)
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
        = MutableStateFlow(PropertyUiState.Loading)
        viewModelScope.launch {
            propertySource.getPropertyById(propertyId)
                .catch { e ->
                     property.value = PropertyUiState.Error(e)
            }
                .collect { propertyReceived ->
                    if(propertyReceived == null)
                        property.value = PropertyUiState.Loading
                    else
                        property.value = PropertyUiState.Success(propertyReceived)
                }
        }
        return property
    }

    fun getProperties(): StateFlow<PropertyListUiState> {
        val propertyList: MutableStateFlow<PropertyListUiState>
                = MutableStateFlow(PropertyListUiState.Loading)
        viewModelScope.launch {
            propertySource.getAllProperties()
                .catch { e ->
                    propertyList.value = PropertyListUiState.Error(e)
                }
                .collect { propertyListReceived ->
                    if (propertyListReceived == null)
                        propertyList.value = PropertyListUiState.Loading
                    else if (propertyListReceived.isEmpty()) {
                        propertyList.value = PropertyListUiState.Empty
                    } else
                        propertyList.value = PropertyListUiState.Success(propertyListReceived)
                }
        }
        return propertyList
    }

    companion object{
        private val TAG = PropertyViewModel::class.java.simpleName
    }
}