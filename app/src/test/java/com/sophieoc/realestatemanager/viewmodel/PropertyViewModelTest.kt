package com.sophieoc.realestatemanager.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.repository.PropertyRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PropertyViewModelTest {
    private lateinit var viewModel : PropertyViewModel
    private var property = spyk<Property>()
    private lateinit var propertySource : PropertyRepository

    companion object{
        const val PROPERTY_ID = "23"
    }
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        propertySource = mockk()
        viewModel = PropertyViewModel(propertySource)
        property.description = "Amazing flat !"
        viewModel.property = property
    }

    @Test
    fun `insert property with success`() {
        val propertyMutable = MutableLiveData(mockk<Property>())
        propertyMutable.value = property
        //insert property
        viewModel.upsertProperty()
        every {  propertySource.upsert(property) } returns propertyMutable
        viewModel.propertySaved.observeForever {
            //check if the property received is the same as the one sent
            assertSame(propertyMutable.value, it)
        }
    }

    @Test
    fun `update property with success`() {
        val propertyMutable = MutableLiveData(mockk<Property>())
        propertyMutable.value = property
        //modify property
        property.description = "Great stuff!"
        viewModel.upsertProperty()
        every { propertySource.upsert(property) } returns propertyMutable
        viewModel.propertySaved.observeForever {
            //check if the description has been changed
            assertSame(propertyMutable.value, it)
            assertSame(propertyMutable.value?.description, it.description)
        }
    }

    @Test
    fun `get property by id with success`() {
        val propertyMutable = MutableLiveData(mockk<Property>())
        propertyMutable.value = property
        property.id = PROPERTY_ID
        //insertProperty
        viewModel.upsertProperty()
        every { propertySource.getPropertyById(PROPERTY_ID) } returns propertyMutable
        viewModel.getPropertyById(PROPERTY_ID).observeForever {
            //check if the description has been changed
            assertSame(propertyMutable.value, it)
            assertSame(propertyMutable.value?.description, it.description)
        }
    }

    @Test
    fun `get all properties with success`() {
        val propertiesMutable = MutableLiveData(mockk<List<Property>>())
        val listProperties = arrayListOf(property, Property())
        propertiesMutable.value = listProperties
        // adding the two properties
        viewModel.upsertProperty()
        viewModel.property = Property()
        viewModel.upsertProperty()
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
        //TODO: use MockWebServer
    }
}