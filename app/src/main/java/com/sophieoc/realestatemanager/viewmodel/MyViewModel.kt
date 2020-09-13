package com.sophieoc.realestatemanager.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.repository.UserRepository

class MyViewModel(private val userSource: UserRepository, val propertyRepository: PropertyRepository): ViewModel() {
    val currentUser = userSource.currentUser

}
