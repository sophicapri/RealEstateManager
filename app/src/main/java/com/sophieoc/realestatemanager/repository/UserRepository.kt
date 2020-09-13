package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import com.sophieoc.realestatemanager.utils.PreferenceHelper

class UserRepository(private val userDao: UserDao) {
    private val userCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("users")
    private val uid = PreferenceHelper.uid
    val currentUserLocal = getUserByIdLocal(uid)
    val currentUserFirestore: LiveData<User> =  getUserByIdFirestore(uid)

    // ROOM
    fun getUserByIdLocal(uid: String): LiveData<UserWithProperties> {
        return userDao.getUserWithPropertiesById(uid)
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

    // FIRESTORE
    fun getUserByIdFirestore(uid: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        userCollectionRef.document(uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) if (task.result != null)
                user.postValue(task.result!!.toObject(User::class.java))
            else if (task.exception != null)
                Log.e("TAG", "getUser " + task.exception!!.message)
        }
        return user
    }

}