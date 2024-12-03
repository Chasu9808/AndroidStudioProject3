package com.sylovestp.firebasetest.testspringrestapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
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

class CommentDetailActivity : AppCompatActivity() {

    private lateinit var writerTextView: TextView
    private lateinit var contentEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var commentId: Long = 0
    private var boardId: Long = 0 // boardId 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_detail)

        jwtToken = getJwtTokenFromSharedPreferences()

        commentId = intent.getLongExtra("COMMENT_ID", -1L)
        boardId = intent.getLongExtra("BOARD_ID", -1L) // boardId 받기
        val writer = intent.getStringExtra("COMMENT_WRITER") ?: "Unknown"
        val content = intent.getStringExtra("COMMENT_CONTENT") ?: ""

        writerTextView = findViewById(R.id.commentWriterTextView)
        contentEditText = findViewById(R.id.commentContentEditText)
        updateButton = findViewById(R.id.updateCommentButton)
        deleteButton = findViewById(R.id.deleteCommentButton)

        writerTextView.text = writer
        contentEditText.setText(content)

        updateButton.setOnClickListener { updateComment() }
        deleteButton.setOnClickListener { deleteComment() }
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun updateComment() {
        val updatedContent = contentEditText.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateComment(
                    token = "Bearer $jwtToken",
                    commentId = commentId,
                    commentDto = CommentDto(
                        id = commentId,
                        boardId = boardId, // boardId 추가
                        content2 = updatedContent
                    )
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@CommentDetailActivity,
                            "댓글이 수정되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(Activity.RESULT_OK) // 결과 설정
                        finish()
                    } else {
                        Toast.makeText(
                            this@CommentDetailActivity,
                            "댓글 수정 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CommentDetailActivity,
                        "댓글 수정 중 오류가 발생했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteComment() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteComment("Bearer $jwtToken", commentId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@CommentDetailActivity,
                            "댓글이 삭제되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(Activity.RESULT_OK) // 결과 설정
                        finish()
                    } else {
                        Toast.makeText(
                            this@CommentDetailActivity,
                            "댓글 삭제 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CommentDetailActivity,
                        "댓글 삭제 중 오류가 발생했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
