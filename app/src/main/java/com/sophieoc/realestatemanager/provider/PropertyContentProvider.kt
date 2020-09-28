package com.sophieoc.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PropertyContentProvider(private val propertyDao: PropertyDao) : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
       context?.let {
            val userId = ContentUris.parseId(uri).toInt()
            val cursor: Cursor = when (userId) {
                -1 -> propertyDao.getPropertiesWithCursor()
                else -> propertyDao.getPropertiesWithCursorForUser(userId.toString())
            }
            cursor.setNotificationUri(it.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        context?.let {
            var id = 0L
            CoroutineScope(Dispatchers.IO).launch {
                if (values != null)
                    id = propertyDao.upsert(Property.fromContentValues(values))
                if (id != 0L)
                    it.contentResolver?.notifyChange(uri, null)
            }.isCompleted.let {
                return ContentUris.withAppendedId(uri, id)
            }
        }
        throw java.lang.IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        context?.let {
            var count = 0
            CoroutineScope(Dispatchers.IO).launch {
                if (values != null)
                    count = propertyDao.upsert(Property.fromContentValues(values)).toInt()
                it.contentResolver?.notifyChange(uri, null)
            }.isCompleted.let {
                return count
            }
        }
        throw java.lang.IllegalArgumentException("Failed to update row into $uri")
    }

    companion object {
        private const val AUTHORITY = "com.sophieoc.realestatemanager.provider"
        private val TABLE_NAME = Property::class.java.simpleName
        val URI_ITEM: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }
}