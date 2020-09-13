package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import kotlinx.coroutines.CoroutineScope

class UserRepository(private val userDao: UserDao) {
    private val userCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("users")
    private val uid = PreferenceHelper.uid

    fun getCurrentUser(): LiveData<User> {
        val currentUser: MutableLiveData<User> = MutableLiveData()
        // TODO: récupérer le user depuis Room
        currentUser.postValue(getUserByIdLocal(uid).value?.user)
        // TODO: le renvoyer à la vue
        // TODO: récupérer le user depuis Firestore
        getUserByIdFirestore(uid, currentUser)
        // TODO: renvoyer à la vue
        return currentUser
    }

    // ROOM
    fun getUserByIdLocal(uid: String): LiveData<UserWithProperties> {
        return userDao.getUserWithPropertiesById(uid)
    }

    fun insert(user: User): Long {
        return userDao.insert(user)
    }

    suspend fun update(user: User): Int {
        return userDao.update(user)
    }

    suspend fun deleteUsers(): Int {
        return userDao.deleteUsers()
    }

    // FIRESTORE
    fun getUserByIdFirestore(uid: String, user: MutableLiveData<User>) {
        userCollectionRef.document(uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                task.result?.toObject(User::class.java)?.let { currentUser ->
                    insert(currentUser)
                    user.postValue(currentUser)
                }
            } else if (task.exception != null)
                Log.e("TAG", "getUser " + task.exception!!.message)
            else if (!task.isSuccessful) {
                createUserInFirestore()
            }
        }
    }

    private fun createUserInFirestore() {
        FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
            val urlPicture = firebaseUser.photoUrl?.toString()
            val uid: String = firebaseUser.uid
            val username: String = firebaseUser.displayName ?: ""
            val email: String = firebaseUser.email ?: ""
            val currentUser = User(uid = uid, username = username, email = email, urlPhoto = urlPicture)
            insert(currentUser)
            PreferenceHelper.uid = uid
        }
    }
}