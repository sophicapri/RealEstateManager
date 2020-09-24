package com.sophieoc.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Property
import com.sophieoc.realestatemanager.AppController.Companion.instance
import com.sophieoc.realestatemanager.room_database.RealEstateDatabase
import com.sophieoc.realestatemanager.room_database.dao.PropertyDao
import com.sophieoc.realestatemanager.room_database.dao.UserDao


class ItemContentProvider(val propertyDao: PropertyDao) : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        if (context != null) {
            val userId = ContentUris.parseId(uri)
           // val cursor: Cursor = propertyDao.getPropertiesWithCursor(userId)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }

        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    companion object {
        private const val AUTHORITY = "com.sophieoc.realestatemanager.provider"
        private val TABLE_NAME = Property::class.java.simpleName
        val URI_ITEM: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }
}