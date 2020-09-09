package com.sophieoc.realestatemanager.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UserDataRepository {
    //to do : create constant for user collection name
    var userCollectionReference: CollectionReference = FirebaseFirestore.getInstance().collection("users")


    fun getData(): String{
        return "My Data"
    }

}