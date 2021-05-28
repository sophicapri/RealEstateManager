package com.sophieoc.realestatemanager.presentation.ui.propertylist

import com.sophieoc.realestatemanager.model.Property

sealed class PropertyListUiState{
    data class Loading(val propertyList: List<Property>) : PropertyListUiState()
    data class Success(val propertyList: List<Property>): PropertyListUiState()
    data class Error(val exception: Throwable): PropertyListUiState()
}
