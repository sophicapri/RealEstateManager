package com.sophieoc.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.sophieoc.realestatemanager.database.RealEstateDatabase
import com.sophieoc.realestatemanager.model.Property
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class PropertyContentProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
       context?.let {
           var cursor: Cursor?
           val userId: Long
           try {
               userId = ContentUris.parseId(uri)
               cursor = RealEstateDatabase.getInstance(it).propertyDao().getPropertiesWithCursorForUser(userId.toString())
           }
           catch (e: NumberFormatException){
               cursor = RealEstateDatabase.getInstance(it).propertyDao().getPropertiesWithCursor()
           }
            cursor?.setNotificationUri(it.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String {
        return "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        context?.let {
            var id : Long? = 0L
            CoroutineScope(Dispatchers.IO).launch {
                if (values != null) {
                    id = RealEstateDatabase.getInstance(it).propertyDao().upsert(Property.fromContentValues(values))
                } else
                if (id != 0L)
                    it.contentResolver?.notifyChange(uri, null)
            }.isCompleted.let {
                return id?.let { uri }
            }
        }
        throw java.lang.IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        context?.let {
            val userId: Long
            var count : Int
            try {
                userId = ContentUris.parseId(uri)
                count = RealEstateDatabase.getInstance(it).propertyDao().deleteById(userId.toString())
            }
            catch (e: NumberFormatException){
                count = RealEstateDatabase.getInstance(it).propertyDao().deleteAll()
            }
            it.contentResolver.notifyChange(uri, null)
            return count
        }
        throw java.lang.IllegalArgumentException("Failed to delete row into $uri")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        context?.let {
            var count = 0
            CoroutineScope(Dispatchers.IO).launch {
                if (values != null)
                    count = RealEstateDatabase.getInstance(it).propertyDao().upsert(Property.fromContentValues(values)).toInt()
                it.contentResolver?.notifyChange(uri, null)
            }.isCompleted.let {
                return count
            }
        }
        throw java.lang.IllegalArgumentException("Failed to update row into $uri")
    }

    companion object {
        private const val AUTHORITY = "com.sophieoc.realestatemanager.provider"
        private val TABLE_NAME = Property::class.java.simpleName.lowercase(Locale.ROOT)
        val URI_PROPERTY: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }
}