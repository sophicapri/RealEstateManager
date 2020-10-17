package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository

class UserViewModel(private val userSource: UserRepository) : ViewModel() {
    val currentUser = userSource.currentUser

    fun getUserById(uid: String): LiveData<UserWithProperties> = userSource.getUserWithProperties(uid)

    fun updateUser(user: User) = userSource.upsertUserInFirestore(user)

}