package com.sophieoc.realestatemanager.ui.propertylist

import com.sophieoc.realestatemanager.model.Property

sealed class PropertyListUiState{
    object Loading : PropertyListUiState()
    object Empty : PropertyListUiState()
    data class Success(val propertyList: List<Property>): PropertyListUiState()
    data class Error(val exception: Throwable): PropertyListUiState()
}
