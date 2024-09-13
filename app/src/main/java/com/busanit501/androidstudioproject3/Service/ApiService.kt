package com.busanit501.androidstudioproject3.Service

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call

interface ApiService {
    @Multipart
    @POST("/classify")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ClassificationResponse>
}