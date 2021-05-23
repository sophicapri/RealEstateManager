package com.sophieoc.realestatemanager.utils

import android.content.Context
import com.sophieoc.realestatemanager.BaseApplication
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): BaseApplication {
        return app as BaseApplication
    }
}

/*

val viewModelModule = module {
    // Specific viewModel pattern to tell Koin how to build ViewModel
    viewModel {
        PropertyViewModel(get())
    }

    viewModel {
        FilterViewModel(get())
    }

    viewModel {
        UserViewModel(get())
    }
}*/
