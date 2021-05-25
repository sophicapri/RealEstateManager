package com.sophieoc.realestatemanager.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.presentation.ui.PropertyViewModel
import com.sophieoc.realestatemanager.presentation.ui.filter.FilterViewModel
import com.sophieoc.realestatemanager.repository.PropertyRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FilterViewModelTest {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var propertyViewModel: PropertyViewModel
    private lateinit var propertySource: PropertyRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        propertySource = mockk()
        propertyViewModel = PropertyViewModel(propertySource)
        filterViewModel = FilterViewModel(propertySource)
    }

    @Test
    fun `get result for search`() {
        val propertyListMutable = MutableLiveData(mockk<List<Property>>())
        propertyListMutable.value = arrayListOf(Property())
        every {
            propertySource.getFilteredProperties(any(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any())
        } returns propertyListMutable
        filterViewModel.startSearch()
        filterViewModel.resultSearch.observeForever { propertyList ->
            //check if the property received is the one expected
            assertSame(propertyListMutable.value, propertyList)
        }
    }
}