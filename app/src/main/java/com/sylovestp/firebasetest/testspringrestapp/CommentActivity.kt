package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.dto.CommentDto
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class CommentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addCommentButton: Button
    private lateinit var updateCommentButton: Button
    private lateinit var deleteCommentButton: Button

    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var boardId: Long = 0 // 게시글 ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        // JWT 토큰 가져오기
        jwtToken = getJwtTokenFromSharedPreferences()

        // Intent로 전달된 게시글 ID 확인
        boardId = intent.getLongExtra("BOARD_ID", 0)
        if (boardId == 0L || jwtToken.isEmpty()) {
            Toast.makeText(this, "올바르지 않은 접근입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // RecyclerView 및 버튼 초기화
        recyclerView = findViewById(R.id.commentRecyclerView)
        addCommentButton = findViewById(R.id.addCommentButton)
        updateCommentButton = findViewById(R.id.updateCommentButton)
        deleteCommentButton = findViewById(R.id.deleteCommentButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 버튼 이벤트 처리
        addCommentButton.setOnClickListener { createComment() }
        updateCommentButton.setOnClickListener { updateComment(1L) } // 예시 ID
        deleteCommentButton.setOnClickListener { deleteComment(1L) } // 예시 ID

        // 댓글 데이터 가져오기
        fetchComments()
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun fetchComments() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCommentsForBoard("Bearer $jwtToken", boardId)
                handleFetchCommentsResponse(response)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CommentActivity, "댓글 데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun handleFetchCommentsResponse(response: Response<List<CommentDto>>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val comments = response.body() ?: emptyList()
                recyclerView.adapter = CommentAdapter(comments)
            } else {
                Toast.makeText(this@CommentActivity, "서버 응답 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createComment() {
        val comment = CommentDto(
            boardId = boardId,
            content2 = "This is a new comment",
            writer = "Android User"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.createComment("Bearer $jwtToken", boardId, comment)
                handleCreateOrUpdateResponse(response, "댓글이 성공적으로 추가되었습니다.")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CommentActivity, "댓글 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateComment(commentId: Long) {
        val comment = CommentDto(
            id = commentId,
            boardId = boardId,
            content2 = "Updated comment content",
            writer = "Android User"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateComment("Bearer $jwtToken", commentId, comment)
                handleCreateOrUpdateResponse(response, "댓글이 성공적으로 수정되었습니다.")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CommentActivity, "댓글 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteComment(commentId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteComment("Bearer $jwtToken", commentId)
                handleDeleteResponse(response)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CommentActivity, "댓글 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun handleCreateOrUpdateResponse(response: Response<*>, successMessage: String) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                Toast.makeText(this@CommentActivity, successMessage, Toast.LENGTH_SHORT).show()
                fetchComments() // 댓글 목록 새로고침
            } else {
                Toast.makeText(this@CommentActivity, "요청 처리 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun handleDeleteResponse(response: Response<*>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                Toast.makeText(this@CommentActivity, "댓글이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                fetchComments() // 댓글 목록 새로고침
            } else {
                Toast.makeText(this@CommentActivity, "댓글 삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
