package com.sophieoc.realestatemanager.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.repository.PropertyRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PropertyViewModelTest {
    private lateinit var viewModel : PropertyViewModel
    private var property = Property()
    private lateinit var propertySource : PropertyRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        propertySource = mockk()
        viewModel = PropertyViewModel(propertySource)
    }

    @Test
    fun `insert property with success`() {
        val propertyMutable = MutableLiveData(mockk<Property>())
        propertyMutable.value = property
        every {  propertySource.upsert(any()) } returns propertyMutable
        viewModel.propertySaved.observeForever {
            //check if the property received is the same as the one sent
            assertSame(propertyMutable.value, it)
        }
    }

    @Test
    fun `update property with success`() {
        val propertyMutable = MutableLiveData(mockk<Property>())
        propertyMutable.value = property
        every { propertySource.upsert(any()) } returns propertyMutable
        viewModel.propertySaved.observeForever {
            assertSame(propertyMutable.value, it)
            assertSame(propertyMutable.value?.description, it.description)
        }
    }

    @Test
    fun `get property by id with success`() {
        val propertyMutable = MutableLiveData(mockk<Property>())
        propertyMutable.value = property
        every { propertySource.getPropertyById(any()) } returns propertyMutable
        viewModel.getPropertyById(String()).observeForever {
            //check if the description has been changed
            assertSame(propertyMutable.value, it)
            assertSame(propertyMutable.value?.description, it.description)
        }
    }

    @Test
    fun `get all properties with success`() {
        val propertiesMutable = MutableLiveData(mockk<List<Property>>())
        propertiesMutable.value = arrayListOf(property, Property())
        every { propertySource.getAllProperties()} returns propertiesMutable
        viewModel.getProperties().observeForever {
            //check if the list is the same
            assertSame(propertiesMutable.value, it)
            assertSame((propertiesMutable.value as ArrayList<Property>).size, it.size)
            assertSame(it.size, 2)
        }
    }

    @Test
    fun `get nearby point of interests with success`() {
        val pointOfInterests = MutableLiveData(mockk<List<PlaceDetails>>())
        pointOfInterests.value = arrayListOf(PlaceDetails())
        every { propertySource.getNearbyPointOfInterests(any())} returns pointOfInterests
        viewModel.getPointOfInterests(String()).observeForever{
            assertSame(pointOfInterests.value, it)
        }
    }
}