package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sophieoc.realestatemanager.api.PlaceApi
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.viewModelModule

class PropertyRepository(private val propertyDao: PropertyDao, val placeApi: PlaceApi) {
    private val propertyCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("properties")
    val propertiesFirestore: LiveData<List<Property>> = getAllPropertiesFirestore()
    val propertiesLocal = propertyDao.getProperties()

    // FIRESTORE
    fun getUserPropertiesById(uid: String): MutableLiveData<List<Property?>> {
        val properties = MutableLiveData<List<Property?>>()
        propertyCollectionRef.whereEqualTo("userId", uid).get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) if (task.result != null)
                properties.postValue(task.result!!.toObjects(Property::class.java))
            else if (task.exception != null)
                Log.e("TAG", "getUserPropertiesById " + task.exception!!.message)
        }
        return properties
    }

    fun getAllPropertiesFirestore(): MutableLiveData<List<Property>> {
        val properties = MutableLiveData<List<Property>>()
        propertyCollectionRef.get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) if (task.result != null)
                properties.postValue(task.result!!.toObjects(Property::class.java))
            else if (task.exception != null)
                Log.e("TAG", "getUserPropertiesById " + task.exception!!.message)
        }
        return properties
    }

    // ROOM
    fun getPropertyById(id: String): LiveData<Property> = propertyDao.getPropertyById(id)

    suspend fun updateProperty(property: Property) = propertyDao.update(property)

}