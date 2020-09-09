package com.sophieoc.realestatemanager

import com.sophieoc.realestatemanager.repository.PropertyDataRepository
import com.sophieoc.realestatemanager.repository.UserDataRepository
import com.sophieoc.realestatemanager.viewmodel.MyViewModel
import org.koin.dsl.module

val appModule = module {
    // single = defines a singleton (only one instance)
    single { UserDataRepository() }

    single { PropertyDataRepository() }

    // factory = creates a new instance every time
    factory { MyViewModel(get(), get()) }
}