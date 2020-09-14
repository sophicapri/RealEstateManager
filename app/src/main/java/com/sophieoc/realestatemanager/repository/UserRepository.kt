package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.room_database.dao.UserDao
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class UserRepository(private val userDao: UserDao) {
    private val userCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("users")
    private val propertyCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("properties")
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val currentUser = getUserWithProperties(uid)

    fun getUserWithProperties(uid: String): MutableLiveData<UserWithProperties>? {
        val currentUser: MutableLiveData<UserWithProperties> = MutableLiveData()
        currentUser.postValue(getUserByIdLocal(uid).value)
        getUserFromFirestore(uid, currentUser)
        return currentUser
    }

    // ROOM
    fun getUserByIdLocal(uid: String): LiveData<UserWithProperties> {
        return userDao.getUserWithPropertiesById(uid)
    }

    private fun insertInRoom(user: User) {
        val job: CompletableJob = Job()
        job.let {
            CoroutineScope(IO + it).launch {
                userDao.insert(user)
                it.complete()
            }
        }
    }

    suspend fun update(user: User): Int {
        return userDao.update(user)
    }

    suspend fun deleteUsers(): Int {
        return userDao.deleteUsers()
    }

    // FIRESTORE
    private fun getUserFromFirestore(uid: String, userMutable: MutableLiveData<UserWithProperties>) {
        userCollectionRef.document(uid).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val userResult = task.result?.toObject(User::class.java)
                userResult?.let { user ->
                    insertInRoom(user)
                    getUserPropertiesFromFirestore(uid, userMutable, user)
                }
                if (userResult == null)
                    saveUserInFirestoreAndRoom()
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
                Log.e("TAG", "getUserPropertiesById " + task.exception!!.message)
        }
    }

    private fun saveUserInFirestoreAndRoom() {
        FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
            val urlPicture = firebaseUser.photoUrl?.toString()
            val uid: String = firebaseUser.uid
            val username: String = firebaseUser.displayName ?: ""
            val email: String = firebaseUser.email ?: ""
            val currentUser = User(uid = uid, username = username, email = email, urlPhoto = urlPicture)
            val userToCreate = MutableLiveData<User>()
            userCollectionRef.document(uid).get().addOnCompleteListener { uidTask: Task<DocumentSnapshot?> ->
                if (uidTask.isSuccessful) {
                    if (uidTask.result != null) userCollectionRef.document(uid).set(currentUser).addOnCompleteListener { userCreationTask: Task<Void?> ->
                        if (userCreationTask.isSuccessful)
                            userToCreate.setValue(currentUser)
                        else if (userCreationTask.exception != null)
                            Log.e("TAG", " createUserInFirestore: " + userCreationTask.exception?.message)
                    }
                } else if (uidTask.exception != null)
                    Log.e("TAG", " createUser: " + uidTask.exception?.message)
            }
            PreferenceHelper.uid = uid
            // Save in room
            insertInRoom(currentUser)
        }
    }

    fun getUsersLocal(): LiveData<List<UserWithProperties>> {
        val result = userDao.getUsersWithProperties()
        println("users = " + result.value?.size)
        return result
    }
}