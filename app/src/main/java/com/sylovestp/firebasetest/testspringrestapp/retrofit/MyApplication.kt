package com.sylovestp.firebasetest.testspringrestapp.retrofit

import android.app.Application
import android.content.Context
import com.sylovestp.firebasetest.testspringrestapp.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application(){

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit_token: Retrofit
    private lateinit var apiService: INetworkService


    val BASE_URL = "http://10.100.201.52:8080"

    var networkService: INetworkService


    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    fun initialize(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .build()

        retrofit_token = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit_token.create(INetworkService::class.java)
    }

    fun getApiService(): INetworkService {
        return apiService
    }

    init {
        networkService = retrofit.create(INetworkService::class.java)
    }
}