package com.sylovestp.firebasetest.testspringrestapp.retrofit

import com.sylovestp.firebasetest.testspringrestapp.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface INetworkService {

    // 이미지 분류
    @Multipart
    @POST("/classify")
    fun predictImage(
        @Part image: MultipartBody.Part? = null
    ): Call<PredictionResult>

    // 사용자 등록
    @Multipart
    @POST("/public/users")
    fun registerUser(
        @Part("user") user: RequestBody,
        @Part profileImage: MultipartBody.Part? = null
    ): Call<ResponseBody>

    // 로그인
    @POST("/generateToken")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // 사용자 페이지 조회 (Paging)
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

    // 도구 리스트 조회
    @GET("api/tools/list")
    fun findAll(@Header("Authorization") token: String): Call<List<Tool>>

    // MyPage 관련 API
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

    // 게시글 CRUD
    @GET("/api/boards")
    suspend fun getAllBoards(
        @Header("Authorization") token: String,
        @Query("searchKeyword") searchKeyword: String?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageResponse<BoardDto>>

    @GET("/api/boards/{id}")
    suspend fun getBoardById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<BoardDto>

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

    // 댓글 CRUD
    @GET("/api/comments/board/{boardId}")
    suspend fun getCommentsForBoard(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long
    ): Response<List<CommentDto>>

    @POST("/api/comments/{boardId}")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long,
        @Body commentDto: CommentDto
    ): Response<ResponseBody>

    @PUT("/api/comments/{commentId}")
    suspend fun updateComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long,
        @Body commentDto: CommentDto
    ): Response<ResponseBody>

    @DELETE("/api/comments/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long
    ): Response<ResponseBody>
}
