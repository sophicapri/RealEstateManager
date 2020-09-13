package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import com.sophieoc.realestatemanager.utils.PreferenceHelper

class UserRepository(val userDao: UserDao) {
    //to do : create constant for user collection name
    var userCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("users")

    val currentUser = {
        var uid = FirebaseAuth.getInstance().currentUser?.uid

        //if (uid == null) {
            uid = PreferenceHelper.uid
            getUserByIdLocal(uid)
       // } else
          //  getUserByIdFirestore(uid)
    }

   /* private fun getUserByIdFirestore(uid: String): MutableLiveData<UserWithProperties?> {
        val userData = MutableLiveData<User?>()

        userCollectionRef.document(uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) if (task.result != null)
                userData.postValue(task.result!!.toObject(User::class.java))
            else if (task.exception != null)
                Log.e("TAG", "getUser" + task.exception!!.message) }
    }

    */

    private fun getUserPropertiesById(uid: String) : MutableLiveData<List<Property?>> {
        val properties = MutableLiveData<List<Property?>>()
        userCollectionRef.document(uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) if (task.result != null)
                properties.postValue(listOf(task.result!!.toObject(Property::class.java)))
            else if (task.exception != null)
                Log.e("TAG", "getUser" + task.exception!!.message) }

        return properties
    }


    suspend fun insert(user: User): Long {
        return userDao.insert(user)
    }

    suspend fun update(user: User): Int {
        return userDao.update(user)
    }

    suspend fun deleteUsers(): Int {
        return userDao.deleteUsers()
    }


    fun getUserByIdLocal(uid: String): LiveData<UserWithProperties> {
        return userDao.getUserWithPropertiesById(uid)
    }

    /*   fun getUsers(): LiveData<List<UserWithProperties>>? {
           return userDao.getUsersWithProperties()
       }
     */

}