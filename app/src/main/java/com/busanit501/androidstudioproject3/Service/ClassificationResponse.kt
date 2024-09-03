package com.busanit501.androidstudioproject3.Service

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ClassificationResponse(
    @SerializedName("confidence") val confidence: Double?,
    @SerializedName("predicted_class_index") val predictedClassIndex: Int?,
    @SerializedName("predicted_class_label") val predictedClassLabel: String?,
    @SerializedName("class_confidences") val classConfidences: Map<String, Double>?
)


object RetrofitClient {
    private const val BASE_URL = "http://10.100.201.52:8080/" // 루트 URL만 지정
    private val client = okhttp3.OkHttpClient.Builder().build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)  // baseUrl에는 루트 경로까지만 지정
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}


