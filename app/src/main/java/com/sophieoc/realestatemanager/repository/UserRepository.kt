package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.sophieoc.realestatemanager.AppController
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.view.activity.MainActivity.Companion.TAG
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class UserRepository(private val userDao: UserDao) {
    private val userCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("users")
    private val propertyCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("properties")
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    val currentUser = getUserWithProperties(getUserId())

    fun getUserWithProperties(uid: String): MutableLiveData<UserWithProperties> {
        val user: MutableLiveData<UserWithProperties> = MutableLiveData()
        getUserFromRoom(uid, user)
        if (PreferenceHelper.internetAvailable)
            getUserFromFirestore(uid, user)
        return user
    }

    // ROOM
    private fun getUserFromRoom(uid: String, userMutable: MutableLiveData<UserWithProperties>) {
        CoroutineScope(IO).launch {
            val userWithProperties = userDao.getUserWithPropertiesById(uid)
            withContext(Dispatchers.Main) {
                userMutable.postValue(userWithProperties)
            }
        }
    }

    private fun upsertInRoom(user: User) {
        CoroutineScope(IO).launch {
            userDao.upsert(user)
        }
    }

    // FIRESTORE
    private fun getUserFromFirestore(uid: String, userMutable: MutableLiveData<UserWithProperties>) {
        userCollectionRef.document(uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val userResult = task.result?.toObject(User::class.java)
                userResult?.let { user ->
                    upsertInRoom(user)
                    getUserPropertiesFromFirestore(uid, userMutable, user)
                }
                if (userResult == null)
                    createUserAndSaveInDB()
            } else if (task.exception != null)
                Log.e("TAG", "getUser " + task.exception!!.message)
        }
    }

    private fun getUserPropertiesFromFirestore(uid: String, userMutable: MutableLiveData<UserWithProperties>, user: User) {
        propertyCollectionRef.whereEqualTo("userId", uid).get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful)
                task.result?.toObjects(Property::class.java)?.let {
                    userMutable.postValue(UserWithProperties(user, it))
                }
            else if (task.exception != null)
                Log.e(TAG, "getUserPropertiesById " + task.exception!!.message)
        }
    }

    private fun createUserAndSaveInDB() {
        firebaseUser?.let { firebaseUser ->
            val urlPicture = firebaseUser.photoUrl.toString()
            val uid: String = firebaseUser.uid
            val username: String = firebaseUser.displayName ?: ""
            val email: String = firebaseUser.email ?: ""
            val user = User(uid = uid, username = username, email = email, urlPhoto = urlPicture)
            val currentUser = UserWithProperties(user, ArrayList())

            upsertUser(currentUser)
        }
    }

    fun upsertUser(user: UserWithProperties): MutableLiveData<UserWithProperties> {
        val userToUpsert: MutableLiveData<UserWithProperties> = MutableLiveData<UserWithProperties>()
        userCollectionRef.document(user.user.uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                if (task.result != null)
                    userCollectionRef.document(user.user.uid).set(user.user).addOnCompleteListener { userUpsertTask: Task<Void?> ->
                        if (userUpsertTask.isSuccessful) {
                            upsertInRoom(user.user)
                            userToUpsert.value = user
                        } else if (userUpsertTask.exception != null)
                            Log.e("TAG", "upsertUserTask " + userUpsertTask.exception?.message)
                    }
            } else if (task.exception != null)
                Log.e("TAG", "upsertUser " + task.exception!!.message)
        }
        return userToUpsert
    }

    private fun getUserId(): String {
        return firebaseUser?.uid ?:PreferenceHelper.currentUserId
    }
}