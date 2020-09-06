package com.sophieoc.realestatemanager.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class PropertyDataRepository {
    var userCollectionReference: CollectionReference = FirebaseFirestore.getInstance().collection("properties")
}