package com.sophieoc.realestatemanager.di

/*
import com.sophieoc.realestatemanager.BuildConfig
import com.sophieoc.realestatemanager.api.PlaceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val API_URL = "https://maps.googleapis.com/maps/api/place/"
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()

    @Singleton
    @Provides
    fun providePlaceService(): PlaceService {
        initApiKey()
        initLogging()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        return retrofit.create(PlaceService::class.java)
    }

    private fun initLogging() {
        logging.level = HttpLoggingInterceptor.Level.BODY
        // httpClient.addInterceptor(logging)
    }

    private fun initApiKey() {
        httpClient.addInterceptor { chain ->
            val original: Request = chain.request()
            val originalHttpUrl: HttpUrl = original.url
            val url: HttpUrl = originalHttpUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.API_KEY)
                .build()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .url(url)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }
}*/
