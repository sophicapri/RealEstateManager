package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.repository.UserRepository
import com.sophieoc.realestatemanager.utils.AbsentLiveData

class UserViewModel(private val userSource: UserRepository) : ViewModel() {
    var currentUser : LiveData<UserWithProperties> = userSource.currentUser
    private val _userToUpdate: MutableLiveData<UserWithProperties> = MutableLiveData()
    val userUpdated: LiveData<UserWithProperties> = Transformations.switchMap(_userToUpdate) {
        if (_userToUpdate.value != null)
            userSource.upsertUser(it)
        else
            AbsentLiveData.create()
    }

    fun getUserById(uid: String): LiveData<UserWithProperties> = userSource.getUserWithProperties(uid)

    fun updateUser(user: UserWithProperties) {
        _userToUpdate.value = user
    }
}