package com.sophieoc.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository
import kotlinx.coroutines.launch

class MyViewModel(private val userSource: UserRepository, private val propertySource: PropertyRepository): ViewModel() {
    val propertiesFirestore = propertySource.getAllPropertiesFirestore()

    fun getCurrentUser(): LiveData<UserWithProperties>? {
        return userSource.currentUser
    }

    fun getUserLocal(uid: String) = userSource.getUserByIdLocal(FirebaseAuth.getInstance().uid.toString())

    fun getUsersLocal(): LiveData<List<UserWithProperties>> { return userSource.getUsersLocal()}

    fun getUser(uid :String) = userSource.getUserWithProperties(uid)

    fun insert(property: Property): LiveData<Property> = propertySource.insert(property)

    fun getPropertyById(propertyId: String): LiveData<Property> {
        return propertySource.getPropertyById(propertyId)
    }

    fun getPropertiesLocal(): LiveData<List<Property>> {
        return propertySource.propertiesLocal
    }

    fun getPropertyByIdLocal(uid: String): LiveData<Property> {
        return propertySource.getPropertyLocal(uid)
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

    fun getUserByIdLocal(uid: String): LiveData<UserWithProperties> {
        return userSource.getUserByIdLocal(uid)
    }
}
