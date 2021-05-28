package com.sophieoc.realestatemanager.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.sophieoc.realestatemanager.database.dao.PropertyDao
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.PROPERTIES_PATH
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.TIMESTAMP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class PropertyRepository(private val propertyDao: PropertyDao) {
    private val propertyCollectionRef: CollectionReference =
        FirebaseFirestore.getInstance().collection(PROPERTIES_PATH)

    fun upsert(property: Property): Flow<Property> {
        val propertyToCreate: MutableStateFlow<Property> = MutableStateFlow(Property())
        propertyCollectionRef.document(property.id).get()
            .addOnCompleteListener { propertyIdTask: Task<DocumentSnapshot?> ->
                if (propertyIdTask.isSuccessful) {
                    if (propertyIdTask.result != null) propertyCollectionRef.document(property.id)
                        .set(property)
                        .addOnCompleteListener { propertyCreationTask: Task<Void?> ->
                            if (propertyCreationTask.isSuccessful) {
                                propertyToCreate.value = property
                                upsertInRoom(property)
                            } else if (propertyCreationTask.exception != null)
                                Log.e(TAG, " createProperty: " +
                                        propertyCreationTask.exception?.message)
                        }
                } else if (propertyIdTask.exception != null) Log.e(
                    "TAG",
                    " createProperty: " + propertyIdTask.exception?.message
                )
            }
        return propertyToCreate
    }

    private fun upsertInRoom(property: Property) {
        CoroutineScope(Dispatchers.IO).launch {
            propertyDao.upsert(property)
        }
    }

    fun getPropertyById(id: String): Flow<Property> {
        val property: MutableStateFlow<Property> = MutableStateFlow(Property())
        getPropertyFromRoom(id, property)
        if (PreferenceHelper.internetAvailable)
            getPropertyFromFirestore(id, property)
        return property
    }

    fun getAllProperties(): Flow<List<Property>> {
        val properties: MutableStateFlow<List<Property>> = MutableStateFlow(emptyList())
        if (!PreferenceHelper.internetAvailable)
            getPropertiesFromRoom(properties)
        else
            getPropertiesFromFirestore(properties)
        return properties
    }

    private fun getPropertiesFromRoom(properties: MutableStateFlow<List<Property>>) {
        CoroutineScope(Dispatchers.IO).launch {
            propertyDao.getProperties().collect { propertyList ->
                properties.value = propertyList
            }
        }
    }

    private fun getPropertiesFromFirestore(properties: MutableStateFlow<List<Property>>) {
        propertyCollectionRef.orderBy(TIMESTAMP, Query.Direction.DESCENDING).get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val propertyResult = task.result?.toObjects(Property::class.java)
                    propertyResult?.let {
                        properties.value = it
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

    private fun getPropertyFromRoom(id: String, property: MutableStateFlow<Property>) {
        CoroutineScope(Dispatchers.IO).launch {
            propertyDao.getPropertyById(id).collect { propertyRoom ->
                property.value = propertyRoom
            }
        }
    }

    private fun getPropertyFromFirestore(id: String, property: MutableStateFlow<Property>) {
        propertyCollectionRef.document(id).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful) {
                    val propertyResult = task.result?.toObject(Property::class.java)
                    propertyResult?.let { it ->
                        property.value = it
                        upsertInRoom(it)
                    }
                } else if (task.exception != null)
                    Log.e(TAG, "getProperty " + task.exception!!.message)
            }
    }

    fun getFilteredProperties(
        propertyType: String?, nbrOfBed: Int?, nbrOfBath: Int?, nbrOfRooms: Int?,
        propertyAvailability: String?, dateOnMarket: Date?, dateSold: Date?,
        priceMin: Int?, priceMax: Int?, surfaceMin: Int?, surfaceMax: Int?, nbrOfPictures: Int?,
        area: String?,
    ): Flow<List<Property>> {
        val properties: MutableStateFlow<List<Property>> = MutableStateFlow(emptyList())
        CoroutineScope(Dispatchers.IO).launch {
            propertyDao.getFilteredList(
                propertyType, nbrOfBed, nbrOfBath, nbrOfRooms, propertyAvailability,
                dateOnMarket, dateSold, priceMin, priceMax, surfaceMin, surfaceMax, nbrOfPictures,
                area
            )
                .collect { propertyList ->
                    properties.value = propertyList
                }
        }
        return properties
    }

    fun getPriceOfPriciestProperty(): MutableStateFlow<Int> {
        val priceMutable : MutableStateFlow<Int> = MutableStateFlow(0)
        CoroutineScope(Dispatchers.IO).launch {
            propertyDao.getPriceOfPriciestProperty().collect { property ->
                priceMutable.value = property
            }
        }
        return priceMutable
    }

    fun getSurfaceOfBiggestProperty(): MutableStateFlow<Int> {
        val surfaceMutable : MutableStateFlow<Int> = MutableStateFlow(0)
        CoroutineScope(Dispatchers.IO).launch {
            propertyDao.getSurfaceOfBiggestProperty().collect { surface ->
                surfaceMutable.value = surface
            }
        }
        return surfaceMutable
    }

    companion object {
        const val TAG = "PropertyRepository"
    }
}