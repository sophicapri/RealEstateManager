package com.sophieoc.realestatemanager.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.sophieoc.realestatemanager.database.dao.UserDao
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.utils.PROPERTIES_PATH
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.TIMESTAMP
import com.sophieoc.realestatemanager.utils.USERS_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserRepository(private val userDao: UserDao) {
    private val userCollectionRef: CollectionReference =
        FirebaseFirestore.getInstance().collection(USERS_PATH)
    private val propertyCollectionRef: CollectionReference =
        FirebaseFirestore.getInstance().collection(PROPERTIES_PATH)
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    val currentUser = getUserWithProperties(getUserId())

    fun getUserWithProperties(uid: String): Flow<UserWithProperties?> {
        val user: MutableStateFlow<UserWithProperties?> =
            MutableStateFlow(null)
        if (!PreferenceHelper.internetAvailable)
            getUserFromRoom(uid, user)
        else
            getUserFromFirestore(uid, user)
        return user
    }

    // ROOM
    private fun getUserFromRoom(uid: String, userMutable: MutableStateFlow<UserWithProperties?>) {
        CoroutineScope(IO).launch {
            userDao.getUserWithPropertiesById(uid).collect { userWithProperties ->
                userMutable.value = userWithProperties
            }
        }
    }

    private fun upsertInRoom(user: User) {
        CoroutineScope(IO).launch {
            userDao.upsert(user)
        }
    }

    // FIRESTORE
    private fun getUserFromFirestore(
        uid: String,
        userMutable: MutableStateFlow<UserWithProperties?>
    ) {
        userCollectionRef.document(uid).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful) {
                    val userResult = task.result?.toObject(User::class.java)
                    userResult?.let { user ->
                        upsertInRoom(user)
                        getUserPropertiesFromFirestore(uid, userMutable, user)
                    }
                    if (userResult == null)
                        saveNewUserInDB(userMutable)
                } else if (task.exception != null) {
                    Log.e("TAG", "getUser " + task.exception!!.message)
                    throw task.exception!!
                }
            }
    }

    private fun getUserPropertiesFromFirestore(
        uid: String,
        userMutable: MutableStateFlow<UserWithProperties?>,
        user: User
    ) {
        propertyCollectionRef.whereEqualTo("userId", uid)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .get().addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful)
                    task.result?.toObjects(Property::class.java)?.let {
                        userMutable.value = UserWithProperties(user, it)
                    }
                else if (task.exception != null)
                    Log.e(TAG, "getUserPropertiesFromFirestore " + task.exception!!.message)
            }
    }

    private fun saveNewUserInDB(userMutable: MutableStateFlow<UserWithProperties?>) {
        firebaseUser?.let { firebaseUser ->
            val urlPicture = firebaseUser.photoUrl.toString()
            val uid: String = firebaseUser.uid
            val username: String = firebaseUser.displayName ?: ""
            val email: String = firebaseUser.email ?: ""
            val user = User(uid = uid, username = username, email = email, urlPhoto = urlPicture)
            val currentUser = UserWithProperties(user, ArrayList())
            PreferenceHelper.currentUserId = uid
            CoroutineScope(IO).launch {
                upsertUser(currentUser).collect { userSaved ->
                    userMutable.value = userSaved
                }
            }
        }
    }

    fun upsertUser(user: UserWithProperties): Flow<UserWithProperties> {
        val userToUpsert = MutableStateFlow(UserWithProperties(User(), emptyList()))
        userCollectionRef.document(user.user.uid).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful && task.result != null) {
                        userCollectionRef.document(user.user.uid).set(user.user)
                            .addOnCompleteListener { userUpsertTask: Task<Void?> ->
                                if (userUpsertTask.isSuccessful) {
                                    upsertInRoom(user.user)
                                    userToUpsert.value = user
                                } else if (userUpsertTask.exception != null)
                                    Log.e(TAG, "upsertUserTask " + userUpsertTask.exception?.message)
                                    throw userUpsertTask.exception!!
                            }
                } else if (task.exception != null) {
                    Log.e(TAG, "upsertUser " + task.exception!!.message)
                    throw task.exception!!
                }
            }
        return userToUpsert
    }

    private fun getUserId(): String {
        return firebaseUser?.uid ?: PreferenceHelper.currentUserId
    }

    companion object {
        private val TAG = UserRepository::class.java.name
    }
}