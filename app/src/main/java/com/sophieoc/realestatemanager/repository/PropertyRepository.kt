package com.sophieoc.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sophieoc.realestatemanager.api.PlaceApi
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import kotlinx.coroutines.*

class PropertyRepository(private val propertyDao: PropertyDao, val placeApi: PlaceApi) {
    private val propertyCollectionRef: CollectionReference = FirebaseFirestore.getInstance().collection("properties")
    val propertiesFirestore: LiveData<List<Property>> = getAllPropertiesFirestore()
    val propertiesLocal = propertyDao.getProperties()

    fun insert(property: Property): MutableLiveData<Property> {
        val propertyToCreate: MutableLiveData<Property> = MutableLiveData<Property>()
        propertyCollectionRef.document(property.id).get().addOnCompleteListener(OnCompleteListener {
            propertyIdTask: Task<DocumentSnapshot?> ->
            if (propertyIdTask.isSuccessful) {
                if (propertyIdTask.result != null) propertyCollectionRef.document(property.id).set(property)
                        .addOnCompleteListener(OnCompleteListener { propertyCreationTask: Task<Void?> ->
                            if (propertyCreationTask.isSuccessful) {
                                propertyToCreate.value = property
                                insertInRoom(property)
                            } else if (propertyCreationTask.exception != null)
                                Log.e("TAG", " createProperty: " + propertyCreationTask.exception?.message)
                        })
            } else if (propertyIdTask.exception != null) Log.e("TAG", " createProperty: " + propertyIdTask.exception?.message)
        })
        return propertyToCreate
    }

    private fun insertInRoom(property: Property) {
        val job: CompletableJob = Job()
       object : LiveData<Long>() {
            override fun onActive() {
                super.onActive()
                job.let {
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val newRowId = propertyDao.insert(property)
                        withContext(Dispatchers.Main) {
                            value = newRowId
                            if (newRowId < 0)
                                Log.e("TAG", "insert property: failed")
                        }
                        it.complete()
                    }
                }
            }
        }
    }


    fun getAllPropertiesFirestore(): MutableLiveData<List<Property>> {
        val properties = MutableLiveData<List<Property>>()
        propertyCollectionRef.get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) if (task.result != null)
                properties.postValue(task.result?.toObjects(Property::class.java))
            else if (task.exception != null)
                Log.e("TAG", "getUserPropertiesById " + task.exception!!.message)
        }
        return properties
    }


    fun getPropertyById(id: String): LiveData<Property>? {
        val property: MutableLiveData<Property>? = getPropertyLocal(id)
        println("propertyDAO description= " + property?.value?.description)
        property?.let { getPropertyFromFirestore(id, it) }
        println("propertyFIRESTORE description= " + property?.value?.description)
        return property
    }

    private fun getPropertyFromFirestore(id: String, property: MutableLiveData<Property>) {
        propertyCollectionRef.document(id).get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val propertyResult = task.result?.toObject(Property::class.java)
                propertyResult?.let { it ->
                    property.value = it
                    insertInRoom(it)
                }
            } else if (task.exception != null)
                Log.e("TAG", "getUser " + task.exception!!.message)
        }
    }

    private fun getPropertyLocal(id: String): MutableLiveData<Property>? {
        val property = MutableLiveData<Property>()
        val mediator = MediatorLiveData<Unit>()
        mediator.addSource(propertyDao.getPropertyById(id)) { property.value = it }
        return property
    }


    suspend fun updateProperty(property: Property) = propertyDao.update(property)

}