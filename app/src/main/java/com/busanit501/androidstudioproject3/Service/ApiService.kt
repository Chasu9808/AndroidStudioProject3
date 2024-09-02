package com.busanit501.androidstudioproject3.Service

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call

interface ApiService {
    @Multipart
    @POST("/classify") // Spring 서버의 엔드포인트 URL에 맞게 변경
    fun uploadImage(
        @Part image: MultipartBody.Part? = null
    ): Call<ClassificationResponse>
}
