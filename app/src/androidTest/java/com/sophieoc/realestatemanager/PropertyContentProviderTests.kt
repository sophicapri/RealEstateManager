package com.sophieoc.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
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
import java.util.*


@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTests {
    // FOR DATA
    private var contentResolver: ContentResolver? = null

    @Before
    fun setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
                RealEstateDatabase::class.java)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
       // contentResolver?.delete(PropertyContentProvider.URI_PROPERTY, null,null)
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
        Assert.assertThat(cursor?.moveToFirst(), Matchers.`is`(true))
        Assert.assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("description")), Matchers.`is`("This is a property"))
        Assert.assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("availability")), Matchers.`is`(Gson().toJson(PropertyAvailability.AVAILABLE)))
        Assert.assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("photos")), Matchers.`is`(Gson().toJson(listOf(Photo(), Photo()))))

        //Delete property created :
        val id = cursor?.getString(cursor.getColumnIndexOrThrow("id"))
        id?.toLong()?.let { ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, it) }?.let {
            val count = contentResolver?.delete(it, null, null)
            Assert.assertThat(count, Matchers.`is`(1))
        }
    }

    // ---
    private fun generateProperty(): ContentValues {
        val values = ContentValues()
        values.put("id", 42L)
        values.put("surface", 1234)
        values.put("description", "This is a property")
        values.put("availability", Gson().toJson(PropertyAvailability.AVAILABLE))
        values.put("dateOnMarket", DATE.time)
        values.put("streetName", "Park Avenue")
        values.put("photos", Gson().toJson(listOf(Photo(), Photo())))
        values.put("userId", DUMMY_USER_ID)
        return values
    }

    //TODO: add dummy user for tests
    companion object {
        // DATA SET FOR TEST
        private const val DUMMY_USER_ID = "12345"
        private val DATE: Date = Date()
    }
}