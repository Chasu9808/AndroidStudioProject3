package com.sylovestp.firebasetest.testspringrestapp


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sylovestp.firebasetest.testspringrestapp.dto.CommentDto
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class CommentActivity : AppCompatActivity() {

    private lateinit var commentListTextView: TextView
    private lateinit var commentContentEditText: EditText
    private lateinit var addCommentButton: Button
    private lateinit var updateCommentButton: Button
    private lateinit var deleteCommentButton: Button

    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var boardId: Long = 0 // 게시글 ID를 전달받아 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        initViews()
        jwtToken = getJwtTokenFromSharedPreferences()

        // Intent로 전달된 게시글 ID 확인
        boardId = intent.getLongExtra("BOARD_ID", 0)
        if (boardId == 0L || jwtToken.isEmpty()) {
            showToast("올바르지 않은 접근입니다.")
            finish()
            return
        }

        fetchComments()
    }

    private fun initViews() {
        commentListTextView = findViewById(R.id.commentListTextView)
        commentContentEditText = findViewById(R.id.commentContentEditText)
        addCommentButton = findViewById(R.id.addCommentButton)
        updateCommentButton = findViewById(R.id.updateCommentButton)
        deleteCommentButton = findViewById(R.id.deleteCommentButton)

        addCommentButton.setOnClickListener { createComment() }
        updateCommentButton.setOnClickListener { updateComment(1L) } // 예시 ID
        deleteCommentButton.setOnClickListener { deleteComment(1L) } // 예시 ID
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
                logError("댓글 데이터를 불러오는 중 오류 발생", e.message)
            }
        }
    }

    private suspend fun handleFetchCommentsResponse(response: Response<List<CommentDto>>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val comments = response.body()
                if (comments != null && comments.isNotEmpty()) {
                    commentListTextView.text = comments.joinToString("\n") { "${it.writer}: ${it.content}" }
                } else {
                    commentListTextView.text = "댓글이 없습니다."
                }
            } else {
                logError("댓글 데이터를 불러오지 못했습니다.", response.errorBody()?.string())
            }
        }
    }

    private fun createComment() {
        val content = commentContentEditText.text.toString()

        if (content.isBlank()) {
            showToast("내용을 입력해주세요.")
            return
        }

        val comment = CommentDto(boardId = boardId, content = content, writer = "Android User")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.createComment("Bearer $jwtToken", boardId, comment)
                handleCreateOrUpdateResponse(response, "댓글이 성공적으로 생성되었습니다.")
            } catch (e: Exception) {
                logError("댓글 생성 중 오류 발생", e.message)
            }
        }
    }

    private fun updateComment(commentId: Long) {
        val content = commentContentEditText.text.toString()

        if (content.isBlank()) {
            showToast("내용을 입력해주세요.")
            return
        }

        val comment = CommentDto(id = commentId, boardId = boardId, content = content, writer = "Android User")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateComment("Bearer $jwtToken", commentId, comment)
                handleCreateOrUpdateResponse(response, "댓글이 성공적으로 수정되었습니다.")
            } catch (e: Exception) {
                logError("댓글 수정 중 오류 발생", e.message)
            }
        }
    }

    private fun deleteComment(commentId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteComment("Bearer $jwtToken", commentId)
                handleDeleteResponse(response)
            } catch (e: Exception) {
                logError("댓글 삭제 중 오류 발생", e.message)
            }
        }
    }

    private suspend fun handleCreateOrUpdateResponse(response: Response<*>, successMessage: String) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                showToast(successMessage)
                fetchComments()
            } else {
                logError("요청 처리 실패", response.errorBody()?.string())
            }
        }
    }

    private suspend fun handleDeleteResponse(response: Response<*>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                showToast("댓글이 성공적으로 삭제되었습니다.")
                fetchComments()
            } else {
                logError("댓글 삭제 실패", response.errorBody()?.string())
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logError(message: String, error: String?) {
        Log.e("CommentActivity", "$message: $error")
        showToast(message)
    }
}
