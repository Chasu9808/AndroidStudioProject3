package com.sylovestp.firebasetest.testspringrestapp.retrofit

import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
import com.sylovestp.firebasetest.testspringrestapp.dto.CommentDto
import com.sylovestp.firebasetest.testspringrestapp.dto.LoginRequest
import com.sylovestp.firebasetest.testspringrestapp.dto.LoginResponse
import com.sylovestp.firebasetest.testspringrestapp.dto.PageResponse
import com.sylovestp.firebasetest.testspringrestapp.dto.PasswordChangeRequest
import com.sylovestp.firebasetest.testspringrestapp.dto.PredictionResult
import com.sylovestp.firebasetest.testspringrestapp.dto.Tool
import com.sylovestp.firebasetest.testspringrestapp.dto.UserDTO
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface INetworkService {


    @Multipart
    @POST("/classify")
    fun predictImage(
        @Header("Authorization") token: String,
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


    @GET("/api/boards")
    suspend fun getAllBoards(
        @Header("Authorization") token: String,
        @Query("searchKeyword") searchKeyword: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageResponse<BoardDto>>

    @POST("/api/boards")
    suspend fun createBoard(
        @Header("Authorization") token: String,
        @Body boardDto: BoardDto
    ): Response<ResponseBody>

    @PUT("/api/boards/{id}")
    suspend fun updateBoard(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body boardDto: BoardDto
    ): Response<ResponseBody>

    @DELETE("/api/boards/{id}")
    suspend fun deleteBoard(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ResponseBody>


    @GET("/api/comments/board/{boardId}")
    suspend fun getCommentsForBoard(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long
    ): Response<List<CommentDto>>

    @POST("/api/comments/create/{boardId}")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long,
        @Body commentDto: CommentDto
    ): Response<ResponseBody>

    @PUT("/api/comments/update/{commentId}")
    suspend fun updateComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long,
        @Body commentDto: CommentDto
    ): Response<ResponseBody>

    @DELETE("/api/comments/delete/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long
    ): Response<ResponseBody>
}
