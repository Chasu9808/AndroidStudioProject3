package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
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
    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var boardId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        jwtToken = getJwtTokenFromSharedPreferences()

        boardId = intent.getLongExtra("BOARD_ID", 0)
        if (boardId == 0L || jwtToken.isEmpty()) {
            Toast.makeText(this, "올바르지 않은 접근입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.commentRecyclerView)
        addCommentButton = findViewById(R.id.addCommentButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        addCommentButton.setOnClickListener { createComment() }

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
                recyclerView.adapter = CommentAdapter(comments) { commentId, writer, content ->
                    val intent = Intent(this@CommentActivity, CommentDetailActivity::class.java).apply {
                        putExtra("COMMENT_ID", commentId)
                        putExtra("COMMENT_WRITER", writer)
                        putExtra("COMMENT_CONTENT", content)
                        putExtra("BOARD_ID", boardId)
                    }
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this@CommentActivity, "서버 응답 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createComment() {
        val comment = CommentDto(
            id = null,
            boardId = boardId,
            content2 = "This is a new comment",
            writer = "Android User"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.createComment("Bearer $jwtToken", boardId, comment)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CommentActivity, "댓글이 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()
                        fetchComments()
                    } else {
                        Toast.makeText(this@CommentActivity, "댓글 추가 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CommentActivity, "댓글 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
