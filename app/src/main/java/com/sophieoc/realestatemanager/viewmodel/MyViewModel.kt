package com.sophieoc.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository
import kotlinx.coroutines.launch

class MyViewModel(private val userSource: UserRepository, private val propertySource: PropertyRepository): ViewModel() {
    // FIRESTORE
    val propertiesFirestore = propertySource.getAllPropertiesFirestore()

    fun getCurrentUser(): LiveData<User> {
        return userSource.getCurrentUser()
    }

    fun getUserByIdLocal(uid: String): LiveData<UserWithProperties> {
        return userSource.getUserByIdLocal(uid)
    }

    fun getUserPropertiesByIdFirestore(uid: String): LiveData<List<Property?>> {
        return propertySource.getUserPropertiesById(uid)
    }

    // ROOM
    val propertiesLocal = propertySource.propertiesLocal

    fun insert(user: User) = viewModelScope.launch {
        val newRowId = userSource.insert(user)
        if (newRowId < 0)
            Log.e("TAG", "insert user: failed")
    }

    fun update(user: User) = viewModelScope.launch {
        val newRowId = userSource.update(user)
        if (newRowId < 0)
            Log.e("TAG", "update user: failed")
    }

    fun deleteUsers() = viewModelScope.launch {
        val newRowId = userSource.deleteUsers()
        if (newRowId < 0)
            Log.e("TAG", "delete users: failed")
    }

    fun getPropertyByIdLocal(id: String) = propertySource.getPropertyById(id)
}
