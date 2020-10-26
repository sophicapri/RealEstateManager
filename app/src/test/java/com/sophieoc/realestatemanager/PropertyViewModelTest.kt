package com.sophieoc.realestatemanager

import com.sophieoc.realestatemanager.repository.PropertyRepository
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock

@RunWith(JUnit4::class)
class PropertyViewModelTest {
    private lateinit var viewModel : PropertyViewModel

    @Mock
    lateinit var propertyRepository: PropertyRepository
}