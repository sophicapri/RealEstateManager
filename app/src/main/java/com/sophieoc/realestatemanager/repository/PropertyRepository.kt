package com.sophieoc.realestatemanager.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sophieoc.realestatemanager.api.PlaceApi
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao

class PropertyRepository(val propertyDao: PropertyDao, val placeApi: PlaceApi) {
    var userCollectionReference: CollectionReference = FirebaseFirestore.getInstance().collection("properties")
}