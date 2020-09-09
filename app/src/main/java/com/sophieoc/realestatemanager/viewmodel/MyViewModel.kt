package com.sophieoc.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import com.sophieoc.realestatemanager.repository.PropertyDataRepository
import com.sophieoc.realestatemanager.repository.UserDataRepository

class MyViewModel(private val userSource: UserDataRepository, val propertyDataRepository: PropertyDataRepository): ViewModel() {

    fun getData(): String{
        return userSource.getData()
    }
}
