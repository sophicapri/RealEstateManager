package com.sophieoc.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.provider.PropertyContentProvider
import com.sophieoc.realestatemanager.room_database.RealEstateDatabase
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTests {
    // FOR DATA
    private var contentResolver: ContentResolver? = null

    companion object {
        private const val DUMMY_USER_ID = 12345L
    }

    @Before
    fun setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
                RealEstateDatabase::class.java)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun insertAndGetProperty() {
        // BEFORE : Get initial cursorCount
        var cursor = contentResolver?.query(PropertyContentProvider.URI_PROPERTY, null, null, null, null)
        val cursorCount = cursor?.count
        // Then : Adding demo item
        contentResolver?.insert(PropertyContentProvider.URI_PROPERTY, generateProperty())
        Thread.sleep(3000)
        // Update cursor count
        cursor = contentResolver?.query(PropertyContentProvider.URI_PROPERTY, null, null, null, null)
        val expectedCount = cursorCount?.let { cursorCount + 1}
        // TEST
        Assert.assertThat(cursor, Matchers.notNullValue())
        Assert.assertThat(cursor?.count, Matchers.`is`(expectedCount))
        // moveToLast() because the properties are in descending order)
        Assert.assertThat(cursor?.moveToLast(), Matchers.`is`(true))
        Assert.assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("description")), Matchers.`is`("This is a property"))
        Assert.assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("availability")), Matchers.`is`(Gson().toJson(PropertyAvailability.AVAILABLE)))
        Assert.assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("photos")), Matchers.`is`(Gson().toJson(listOf(Photo(), Photo()))))

        //Delete property created :
        deleteProperty(cursor)
    }

    @Test
    fun insertAndGetPropertyForUser() {
        // BEFORE : Adding demo item
        contentResolver?.insert(PropertyContentProvider.URI_PROPERTY, generateProperty())
        Thread.sleep(3000)

        // Then : get value for User
        val cursor = contentResolver?.query(ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, DUMMY_USER_ID), null, null, null, null)

        // TEST
        Assert.assertThat(cursor?.moveToFirst(), Matchers.`is`(true))
        Assert.assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("description")), Matchers.`is`("This is a property"))

        //Delete property created :
        deleteProperty(cursor)
    }

    // ---
    private fun generateProperty(): ContentValues {
        val values = ContentValues()
        values.put("id", 42L)
        values.put("description", "This is a property")
        values.put("availability", Gson().toJson(PropertyAvailability.AVAILABLE))
        values.put("photos", Gson().toJson(listOf(Photo(), Photo())))
        values.put("userId", DUMMY_USER_ID.toString())
        return values
    }

    private fun deleteProperty(cursor: Cursor?) {
        val id = cursor?.getString(cursor.getColumnIndexOrThrow("id"))
        id?.toLong()?.let { ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, it) }?.let {
            val count = contentResolver?.delete(it, null, null)
            Assert.assertThat(count, Matchers.`is`(1))
        }
    }
}