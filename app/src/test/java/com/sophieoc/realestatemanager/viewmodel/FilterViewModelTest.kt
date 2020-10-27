package com.sophieoc.realestatemanager.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sophieoc.realestatemanager.model.EntriesFilter
import com.sophieoc.realestatemanager.model.Property
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
    private var entriesFilter = EntriesFilter()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        propertySource = mockk()
        propertyViewModel = PropertyViewModel(propertySource)
        filterViewModel = FilterViewModel(propertySource)
        filterViewModel.entries = entriesFilter
    }

    @Test
    fun `get result for search`() {
        val propertyListMutable = MutableLiveData(mockk<List<Property>>())
        propertyListMutable.value = arrayListOf(Property())
        every {
            propertySource.getFilteredProperties(propertyType = entriesFilter.propertyType,
                    nbrOfBed = entriesFilter.nbrOfBed, nbrOfBath = entriesFilter.nbrOfBath, nbrOfRooms = entriesFilter.nbrOfRoom,
                    propertyAvailability = entriesFilter.propertyAvailability, dateOnMarket = entriesFilter.dateOnMarket,
                    dateSold = entriesFilter.dateSold, priceMin = entriesFilter.priceMin, priceMax = entriesFilter.priceMax,
                    surfaceMin = entriesFilter.surfaceMin, surfaceMax = entriesFilter.surfaceMax,
                    nbrOfPictures = entriesFilter.nbrOfPictures, park = entriesFilter.park,
                    school = entriesFilter.school, store = entriesFilter.store, area = entriesFilter.area)
        } returns propertyListMutable
        filterViewModel.startSearch()
        filterViewModel.resultSearch.observeForever {
            //check if the property received is the one expected
            assertSame(propertyListMutable.value, it)
        }
    }
}