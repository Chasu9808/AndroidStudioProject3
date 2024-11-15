package com.sylovestp.firebasetest.testspringrestapp.retrofit

import com.sylovestp.firebasetest.testspringrestapp.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface INetworkService {

    // 기존 기능 유지
    @Multipart
    @POST("/classify")
    fun predictImage(
        @Part image: MultipartBody.Part? = null
    ): Call<PredictionResult>

    @Multipart
    @POST("/public/users")
    fun registerUser(
        @Part("user") user: RequestBody,
        @Part profileImage: MultipartBody.Part? = null
    ): Call<ResponseBody>

    @POST("/generateToken")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @DELETE("api/users/mypage/deleteAccount")
    fun deleteAccount(@Header("Authorization") token: String): Call<ResponseBody>

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

    @GET("api/tools/list")
    fun findAll(@Header("Authorization") token: String): Call<List<Tool>>

    // 수정된 MyPage 기능
    @GET("/api/users/mypage")
    fun getMyPage(@Header("Authorization") token: String): Call<UserItem>

    @PUT("/api/users/mypage/edit")
    fun editUserField(
        @Header("Authorization") token: String,
        @Body updates: Map<String, String>
    ): Call<ResponseBody>

    @PUT("/api/users/mypage/changePassword")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body passwords: Map<String, String>
    ): Call<ResponseBody>

    @DELETE("/api/users/mypage/deleteAccount")
    fun deleteAccount(
        @Header("Authorization") token: String,
        @Query("password") password: String
    ): Call<ResponseBody>

    // Board 관련 기능
    @GET("/api/boards")
    fun getAllBoards(
        @Header("Authorization") token: String,
        @Query("searchKeyword") searchKeyword: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<PageResponse<BoardDto>>

    @GET("/api/boards/{id}")
    fun getBoardById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<BoardDto>

    @POST("/api/boards")
    fun createBoard(
        @Header("Authorization") token: String,
        @Body boardDto: BoardDto
    ): Call<ResponseBody>

    @PUT("/api/boards/update")
    fun updateBoard(
        @Header("Authorization") token: String,
        @Body boardDto: BoardDto,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>

    @DELETE("/api/boards/{id}")
    fun deleteBoard(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<ResponseBody>

    // Comment 관련 기능
    @POST("/api/comments/create/{boardId}")
    fun createComment(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long,
        @Body commentDto: CommentDto
    ): Call<ResponseBody>

    @PUT("/api/comments/update/{commentId}")
    fun updateComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long,
        @Body commentDto: CommentDto
    ): Call<ResponseBody>

    @DELETE("/api/comments/delete/{commentId}")
    fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long
    ): Call<ResponseBody>
}
