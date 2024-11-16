package com.sylovestp.firebasetest.testspringrestapp.retrofit

import com.sylovestp.firebasetest.testspringrestapp.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface INetworkService {

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

    // MyPage 관련 메서드
    @GET("/api/users/mypage")
    fun getMyPage(@Header("Authorization") token: String): Call<UserDTO>

    @PUT("/api/users/mypage/edit")
    fun editUserField(
        @Header("Authorization") token: String,
        @Body updates: Map<String, String>
    ): Call<ResponseBody>

    @POST("/api/users/mypage/changePassword")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body passwordDetails: PasswordChangeRequest
    ): Call<ResponseBody>

    @POST("/api/users/mypage/deleteAccount")
    fun deleteAccount(
        @Header("Authorization") token: String,
        @Body passwordDetails: Map<String, String>
    ): Call<ResponseBody>


    // Board 관련 메서드
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
        @Body boardDto: BoardDto
    ): Call<ResponseBody>

    @DELETE("/api/boards/{id}")
    fun deleteBoard(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<ResponseBody>

    // Comment 관련 메서드
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
