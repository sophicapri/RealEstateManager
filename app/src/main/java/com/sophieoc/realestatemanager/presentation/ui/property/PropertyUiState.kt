package com.sophieoc.realestatemanager.presentation.ui.property

import com.sophieoc.realestatemanager.model.Property

sealed class PropertyUiState {
    object Loading: PropertyUiState()
    data class Success(val property: Property) : PropertyUiState()
    data class Error(val exception: Throwable): PropertyUiState()
}