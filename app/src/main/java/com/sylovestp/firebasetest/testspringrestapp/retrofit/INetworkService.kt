package com.sylovestp.firebasetest.testspringrestapp.retrofit

import com.sylovestp.firebasetest.testspringrestapp.dto.LoginRequest
import com.sylovestp.firebasetest.testspringrestapp.dto.LoginResponse
import com.sylovestp.firebasetest.testspringrestapp.dto.PageResponse
import com.sylovestp.firebasetest.testspringrestapp.dto.PredictionResult
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface INetworkService {

    @Multipart
    @POST("/api/ai/predict")
//    @POST("/public/users/predict")
//    fun registerUser(@Body userDTO: UserDTO): Call<Void>
    fun predictImage(
//        @Part("user") user: RequestBody?,          // JSON 데이터
        @Part image: MultipartBody.Part? = null    // 파일 데이터 (Optional)
    ): Call<PredictionResult>

    @Multipart
    @POST("/public/users")
//    fun registerUser(@Body userDTO: UserDTO): Call<Void>
    fun registerUser(
        @Part("user") user: RequestBody,          // JSON 데이터
        @Part profileImage: MultipartBody.Part? = null    // 파일 데이터 (Optional)
    ): Call<ResponseBody>

    @POST("/generateToken")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("/api/users/page")
    fun getItems(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<PageResponse<UserItem>>

    @GET("/api/users/page")
    suspend fun getItems2(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageResponse<UserItem>>
}