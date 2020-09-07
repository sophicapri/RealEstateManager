package com.sophieoc.realestatemanager.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.sophieoc.realestatemanager.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

object PlaceService {
    private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    private const val API_URL = "https://maps.googleapis.com/maps/api/place/"
    private fun initLogging() {
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(logging)
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

    fun <S> createService(serviceClass: Class<S>?): S {
        initApiKey()
        initLogging()
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        return retrofit.create(serviceClass)
    }
}