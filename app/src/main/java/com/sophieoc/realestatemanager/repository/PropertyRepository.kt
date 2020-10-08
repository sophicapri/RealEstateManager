package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sophieoc.realestatemanager.api.PlaceApi
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class PropertyRepository(private val propertyDao: PropertyDao, val placeApi: PlaceApi) {
    private val propertyCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("properties")

    fun upsert(property: Property): MutableLiveData<Property> {
        val propertyToCreate: MutableLiveData<Property> = MutableLiveData<Property>()
        propertyCollectionRef.document(property.id).get().addOnCompleteListener(OnCompleteListener { propertyIdTask: Task<DocumentSnapshot?> ->
            if (propertyIdTask.isSuccessful) {
                if (propertyIdTask.result != null) propertyCollectionRef.document(property.id).set(property)
                        .addOnCompleteListener(OnCompleteListener { propertyCreationTask: Task<Void?> ->
                            if (propertyCreationTask.isSuccessful) {
                                propertyToCreate.postValue(property)
                                upsertInRoom(property)
                            } else if (propertyCreationTask.exception != null)
                                Log.e("TAG", " createProperty: " + propertyCreationTask.exception?.message)
                        })
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
        property.postValue(getPropertyFromRoom(id).value)
        getPropertyFromFirestore(id, property)
        return property
    }

    fun getAllProperties(): LiveData<List<Property>> {
        val properties: MutableLiveData<List<Property>> = MutableLiveData()
        getPropertiesFromRoom(properties)
        println("value = ${getPropertiesFromRoom(properties)}")
        //getPropertiesFromFirestore(properties)
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
        propertyCollectionRef.get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                val propertyResult = task.result?.toObjects(Property::class.java)
                propertyResult?.let {
                    properties.postValue(it)
                    updateAllProperties(it.toList())
                }
            } else if (task.exception != null)
                Log.e("TAG", "getUserPropertiesById " + task.exception!!.message)
        }
    }

    fun updateAllProperties(mutableList: List<Property>) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = propertyDao.upsert(mutableList[0])
            println("value id = $id")
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

    fun getNearbyPointOfInterests(location: String): MutableLiveData<List<PlaceDetails>> {
        return object : MutableLiveData<List<PlaceDetails>>() {
            override fun onActive() {
                super.onActive()
                CoroutineScope(Dispatchers.IO).launch {
                    val placeDetailsList = placeApi.getNearbyPlaces(location).placeDetails
                    withContext(Main) {
                        value = placeDetailsList
                    }
                }
            }
        }
    }

    fun getFilteredProperties(
            propertyType: String?, nbrOfBed: Int?, nbrOfBath: Int?,
            propertyAvailability: String?, dateOnMarket: Date?, dateSold: Date?,
            priceMin: Int, priceMax: Int, surfaceMin: Int, surfaceMax: Int,
            pointOfInterests: String?,
    ): MutableLiveData<List<Property>> {
        val properties: MutableLiveData<List<Property>> = MutableLiveData()

        getFilteredPropertiesFromRoom(properties, propertyType, nbrOfBed, nbrOfBath, propertyAvailability,
                dateOnMarket, dateSold, priceMin, priceMax, surfaceMin, surfaceMax, pointOfInterests)
        //getFilteredPropertiesFromFirestore(properties)
        return properties
    }

    private fun getFilteredPropertiesFromRoom(
            properties: MutableLiveData<List<Property>>, propertyType: String?, nbrOfBed: Int?,
            nbrOfBath: Int?, propertyAvailability: String?, dateOnMarket: Date?, dateSold: Date?, priceMin: Int,
            priceMax: Int, surfaceMin: Int, surfaceMax: Int, pointOfInterests: String?,
    ) {
        val queryPair = getQuery(propertyType, nbrOfBed, nbrOfBath, propertyAvailability,
                dateOnMarket, dateSold, priceMin, priceMax, surfaceMin, surfaceMax, pointOfInterests)
        val query = SimpleSQLiteQuery(queryPair.first)
        println("${query.sql}")

        CoroutineScope(Dispatchers.IO).launch {
            val propertyList = propertyDao.getFilteredList(query)
            withContext(Main) {
                properties.postValue(propertyList)
                println("property list = ")
            }
        }
    }

    private fun getQuery(propertyType: String?, nbrOfBed: Int?, nbrOfBath: Int?, propertyAvailability: String?, dateOnMarket: Date?, dateSold: Date?, priceMin: Int, priceMax: Int, surfaceMin: Int, surfaceMax: Int, pointOfInterests: String?): Pair<String, List<Any>> {
        var queryString = String()
        // List of bind parameters
        val args = ArrayList<Any>()
        var containsCondition = false

        // Optional parts are added to query string and to args upon here
        queryString += "SELECT * FROM PROPERTY";

        propertyType?.let {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }

            queryString += " type = $it"
            args.add(it)
        }

        nbrOfBed?.let {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }

            queryString += " number_of_bedrooms = $it"
            args.add(it)
        }

        nbrOfBath?.let {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }

            queryString += " number_of_bathrooms = $it"
            args.add(it)
        }
        propertyAvailability?.let {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }

            queryString += " availability = $it"
            args.add(it)
        }
        dateOnMarket?.let {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }

            queryString += " date_on_market >= $it"
            args.add(it)
        }
        dateSold?.let {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }

            queryString += " date_sold >= $it"
            args.add(it)
        }

        // handle price
        if (containsCondition) {
            queryString += " AND";
        } else {
            queryString += " WHERE";
            containsCondition = true;
        }
        queryString += " price > $priceMin AND price < $priceMax"

        // surface
        if (containsCondition) {
            queryString += " AND";
        } else {
            queryString += " WHERE";
            containsCondition = true;
        }
        queryString += " surface > $surfaceMin AND surface < $surfaceMax"

        /*     pointOfInterests?. let {
                 if (containsCondition) {
                     queryString += " AND";
                 } else {
                     queryString += " WHERE";
                     containsCondition = true;
                 }

                 queryString += " number_of_bedrooms = ? "
                 args.add(it)
             }

         */
        queryString += ";";
        return Pair(queryString, args)
    }

    private fun getFilteredPropertiesFromFirestore(properties: MutableLiveData<List<Property>>): LiveData<List<Property>> {
        return MutableLiveData<List<Property>>()
    }
}