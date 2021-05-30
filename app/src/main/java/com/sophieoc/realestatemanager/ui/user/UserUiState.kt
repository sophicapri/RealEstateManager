package com.sophieoc.realestatemanager.ui.user

import com.sophieoc.realestatemanager.model.UserWithProperties

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val userWithProperties: UserWithProperties) : UserUiState()
    data class Error(val exception: Throwable) : UserUiState()
}
