package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.core.OrderBy
import com.sophieoc.realestatemanager.api.PlaceApi
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.TIMESTAMP
import com.sophieoc.realestatemanager.utils.Utils
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import kotlin.collections.ArrayList


class PropertyRepository(private val propertyDao: PropertyDao, val placeApi: PlaceApi) {
    private val propertyCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("properties")

    fun upsert(property: Property): MutableLiveData<Property> {
        val propertyToCreate: MutableLiveData<Property> = MutableLiveData<Property>()
        propertyCollectionRef.document(property.id).get().addOnCompleteListener(OnCompleteListener { propertyIdTask: Task<DocumentSnapshot?> ->
            if (propertyIdTask.isSuccessful) {
                if (propertyIdTask.result != null) propertyCollectionRef.document(property.id).set(property)
                        .addOnCompleteListener { propertyCreationTask: Task<Void?> ->
                            if (propertyCreationTask.isSuccessful) {
                                propertyToCreate.postValue(property)
                                upsertInRoom(property)
                            } else if (propertyCreationTask.exception != null)
                                Log.e(TAG, " createProperty: " + propertyCreationTask.exception?.message)
                        }
            } else if (propertyIdTask.exception != null) Log.e("TAG", " createProperty: " + propertyIdTask.exception?.message)
        })
        return propertyToCreate
    }

    private fun upsertInRoom(property: Property) {
        CoroutineScope(Dispatchers.IO).launch {
            propertyDao.upsert(property)
        }
    }

    fun getPropertyById(id: String): MutableLiveData<Property> {
        val property: MutableLiveData<Property> = MutableLiveData()
        getPropertyFromRoom(id, property)
        if (PreferenceHelper.internetAvailable)
            getPropertyFromFirestore(id, property)
        return property
    }

    fun getAllProperties(): LiveData<List<Property>> {
        val properties: MutableLiveData<List<Property>> = MutableLiveData()
        getPropertiesFromRoom(properties)
        if (PreferenceHelper.internetAvailable)
            getPropertiesFromFirestore(properties)
        return properties
    }

    private fun getPropertiesFromRoom(properties: MutableLiveData<List<Property>>) {
        CoroutineScope(Dispatchers.IO).launch {
            val propertyList = propertyDao.getProperties()
            withContext(Main) {
                properties.postValue(propertyList)
            }
        }
    }

    private fun getPropertiesFromFirestore(properties: MutableLiveData<List<Property>>) {
        propertyCollectionRef.orderBy(TIMESTAMP, Query.Direction.DESCENDING).get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                val propertyResult = task.result?.toObjects(Property::class.java)
                propertyResult?.let {
                    properties.postValue(it)
                    updateAllProperties(it.toList())
                }
            } else if (task.exception != null)
                Log.e(TAG, "getUserPropertiesById " + task.exception!!.message)
        }
    }

    private fun updateAllProperties(mutableList: List<Property>) {
        mutableList.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                propertyDao.upsert(it)
            }
        }
    }

    private fun getPropertyFromRoom(id: String, property: MutableLiveData<Property>) {
        CoroutineScope(Dispatchers.IO).launch {
            val propertyRoom = propertyDao.getPropertyById(id)
            withContext(Main) {
                property.postValue(propertyRoom)
            }
        }
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
                Log.e(TAG, "getProperty " + task.exception!!.message)
        }
    }

    fun getNearbyPointOfInterests(location: String): MutableLiveData<List<PlaceDetails>> {
        return object : MutableLiveData<List<PlaceDetails>>() {
            override fun onActive() {
                super.onActive()
                CoroutineScope(Dispatchers.IO).launch {
                    val parkList = placeApi.getNearbyParks(location).placeDetails
                    val storeList = placeApi.getNearbyStores(location).placeDetails
                    val schoolList = placeApi.getNearbySchools(location).placeDetails
                    withContext(Main) {
                        val placeDetailsList = ArrayList<PlaceDetails>()
                        storeList?.let {
                            if (it.size >= 5)
                                placeDetailsList.addAll(it.subList(0, 5))
                            else
                                placeDetailsList.addAll(it)
                        }
                        schoolList?.let {
                            if (it.size >= 5)
                                placeDetailsList.addAll(it.subList(0, 5))
                            else
                                placeDetailsList.addAll(it)
                        }
                        parkList?.let {
                            if (parkList.size >= 5)
                                placeDetailsList.addAll(it.subList(0, 5))
                            else
                                placeDetailsList.addAll(it)
                        }
                        value = placeDetailsList
                    }
                }
            }
        }
    }

    fun getFilteredProperties(
            propertyType: String?, nbrOfBed: Int?, nbrOfBath: Int?, nbrOfRooms: Int?,
            propertyAvailability: String?, dateOnMarket: Date?, dateSold: Date?,
            priceMin: Int, priceMax: Int, surfaceMin: Int, surfaceMax: Int, nbrOfPictures: Int?,
            park: String?, school: String?, store: String?, area: String?,
    ): MutableLiveData<List<Property>> {
        val properties: MutableLiveData<List<Property>> = MutableLiveData()
        CoroutineScope(Dispatchers.IO).launch {
            val propertyList = propertyDao.getFilteredList(propertyType, nbrOfBed, nbrOfBath, nbrOfRooms, propertyAvailability,
                    dateOnMarket, dateSold, priceMin, priceMax, surfaceMin, surfaceMax, nbrOfPictures, park, school, store, area)
            withContext(Main) {
                properties.postValue(propertyList)
            }
        }
        return properties
    }

    companion object{
        const val TAG = "PropertyRepository"
    }
}