package com.sophieoc.realestatemanager.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.presentation.ui.userproperty.UserUiState
import com.sophieoc.realestatemanager.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userSource: UserRepository) : ViewModel() {
    private val _currentUser : MutableStateFlow<UserUiState>
        get() {
            val userMutableStateFlow: MutableStateFlow<UserUiState> =
                MutableStateFlow(UserUiState.Loading)
                viewModelScope.launch {
                userSource.currentUser.catch { e -> UserUiState.Error(e) }
                    .collect { userMutableStateFlow.value = UserUiState.Success(it) }
            }
         return userMutableStateFlow
    }
    val currentUser : StateFlow<UserUiState>
        get() = _currentUser
    val userUpdated: StateFlow<UserUiState> = _currentUser

    fun getUserById(uid: String): StateFlow<UserUiState> {
        val userMutable : MutableStateFlow<UserUiState> = MutableStateFlow(UserUiState.Loading)
        viewModelScope.launch {
            userSource.getUserWithProperties(uid)
                .catch { e -> userMutable.value = UserUiState.Error(e) }
                .collect { user ->
                userMutable.value = UserUiState.Success(user)
            }
        }
        return userMutable
    }

    fun updateUser(user: UserWithProperties) {
        viewModelScope.launch {
            userSource.upsertUser(user).catch { e ->
                _currentUser.value = UserUiState.Error(e)
            }
                .collect {
                _currentUser.value = UserUiState.Success(user)
            }
        }
    }
}