package com.busanit501.androidstudioproject3.Service

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ClassificationResponse(

    @SerializedName("predictedLabel") val predictedLabel: String
)


object RetrofitClient {
    private const val BASE_URL = "http://10.100.201.52:8080/"
    private val client = okhttp3.OkHttpClient.Builder().build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}


