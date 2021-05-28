package com.sophieoc.realestatemanager.presentation.ui.property

import com.sophieoc.realestatemanager.model.Property

sealed class PropertyUiState {
    data class Success(val property: Property) : PropertyUiState()
    data class Loading(val property: Property) : PropertyUiState()
    data class Error(val exception: Throwable): PropertyUiState()
}