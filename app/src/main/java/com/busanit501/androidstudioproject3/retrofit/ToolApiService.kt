package com.busanit501.androidstudioproject3.retrofit

import com.busanit501.androidstudioproject3.dto.Tool
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ToolApiService {
    @GET("tools")
    fun findAll(): Call<List<Tool>>

    @GET("tools/{id}")
    fun findById(@Path("id") id: Long): Call<Tool>
}