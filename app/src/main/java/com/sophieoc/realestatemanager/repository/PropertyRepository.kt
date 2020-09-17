package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sophieoc.realestatemanager.api.PlaceApi
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.json_to_java.Location
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.model.json_to_java.PlacesResult
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class PropertyRepository(private val propertyDao: PropertyDao, val placeApi: PlaceApi) {
    private var job: CompletableJob? = null
    private val propertyCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("properties")

    fun upsert(property: Property): MutableLiveData<Property> {
        val propertyToCreate: MutableLiveData<Property> = MutableLiveData<Property>()
        propertyCollectionRef.document(property.id).get().addOnCompleteListener(OnCompleteListener { propertyIdTask: Task<DocumentSnapshot?> ->
            if (propertyIdTask.isSuccessful) {
                if (propertyIdTask.result != null) propertyCollectionRef.document(property.id).set(property)
                        .addOnCompleteListener(OnCompleteListener { propertyCreationTask: Task<Void?> ->
                            if (propertyCreationTask.isSuccessful) {
                                propertyToCreate.postValue(property)
                                println("updated PROP")
                                upsertInRoom(property)
                            } else if (propertyCreationTask.exception != null)
                                Log.e("TAG", " createProperty: " + propertyCreationTask.exception?.message)
                        })
            } else if (propertyIdTask.exception != null) Log.e("TAG", " createProperty: " + propertyIdTask.exception?.message)
        })
        return propertyToCreate
    }

    private fun upsertInRoom(property: Property) {
        job = Job()
        job?.let {
            CoroutineScope(Dispatchers.IO + it).launch {
                propertyDao.upsert(property)
                it.complete()
            }
        }
    }

    fun getPropertyById(id: String): LiveData<Property> {
        val property: MutableLiveData<Property> = MutableLiveData()
        property.postValue(getPropertyFromRoom(id).value)
        getPropertyFromFirestore(id, property)
        return property
    }

    fun getAllProperties(): MutableLiveData<List<Property>> {
        val properties: MutableLiveData<List<Property>> = MutableLiveData()
        properties.postValue(getPropertiesFromRoom().value)
        getPropertiesFromFirestore(properties)
        return properties
    }

    private fun getPropertiesFromRoom() = propertyDao.getProperties()

    private fun getPropertiesFromFirestore(properties: MutableLiveData<List<Property>>) {
        propertyCollectionRef.get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                val propertyResult = task.result?.toObjects(Property::class.java)
                propertyResult?.let {
                    properties.postValue(it)
                }
            } else if (task.exception != null)
                Log.e("TAG", "getUserPropertiesById " + task.exception!!.message)
        }
    }

    private fun getPropertyFromRoom(id: String): LiveData<Property> {
        return propertyDao.getPropertyById(id)
    }

    private fun getPropertyFromFirestore(id: String, property: MutableLiveData<Property>) {
        propertyCollectionRef.document(id).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val propertyResult = task.result?.toObject(Property::class.java)
                propertyResult?.let { it ->
                    property.postValue(it)
                    upsertInRoom(it)
                }
            } else if (task.exception != null)
                Log.e("TAG", "getUser " + task.exception!!.message)
        }
    }

    fun getNearbyPointOfInterests(location: String): LiveData<List<PlaceDetails>> {
        job = Job()
        return object : LiveData<List<PlaceDetails>>() {
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val placeDetailsList = placeApi.getNearbyPlaces(location).placeDetails
                        withContext(Main){
                            value = placeDetailsList
                            it.complete()
                        }
                    }
                }
            }
        }
    }

    fun cancelJobs() = job?.cancel()
}