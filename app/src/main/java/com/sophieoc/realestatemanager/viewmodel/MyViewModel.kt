package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository
import java.util.*

class MyViewModel(private val userSource: UserRepository, private val propertySource: PropertyRepository) : ViewModel() {
    val currentUser = userSource.currentUser

    fun getUserById(uid: String): LiveData<UserWithProperties> = userSource.getUserWithProperties(uid)

    fun updateUser(user: User) = userSource.upsertUserInFirestore(user)

}
