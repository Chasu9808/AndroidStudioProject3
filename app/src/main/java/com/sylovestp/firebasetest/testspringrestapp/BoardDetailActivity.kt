package com.sylovestp.firebasetest.testspringrestapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
import com.sylovestp.firebasetest.testspringrestapp.dto.CommentDto
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BoardDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var writerTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var addCommentButton: Button
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter

    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var boardId: Long = -1L // 기본값으로 Long 타입 설정
    private val commentList = mutableListOf<CommentDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)

        jwtToken = getJwtTokenFromSharedPreferences()

        boardId = intent.getLongExtra("boardId", -1L)
        if (boardId == -1L) {
            showToast("게시판 ID가 유효하지 않습니다.")
            finish()
            return
        }

        val boardTitle = intent.getStringExtra("boardTitle")
        val boardWriter = intent.getStringExtra("boardWriter")
        val boardContent = intent.getStringExtra("boardContent")

        titleTextView = findViewById(R.id.detailBoardTitleTextView)
        writerTextView = findViewById(R.id.detailBoardWriterTextView)
        contentTextView = findViewById(R.id.detailBoardContentTextView)
        editButton = findViewById(R.id.editBoardButton)
        deleteButton = findViewById(R.id.deleteBoardButton)
        addCommentButton = findViewById(R.id.addCommentButton)
        commentRecyclerView = findViewById(R.id.commentRecyclerView)

        titleTextView.text = boardTitle
        writerTextView.text = "작성자: $boardWriter"
        contentTextView.text = boardContent

        editButton.setOnClickListener { showEditDialog() }
        deleteButton.setOnClickListener { deleteBoard() }
        addCommentButton.setOnClickListener { showAddCommentDialog() }

        setupCommentRecyclerView()
        loadComments()
    }

    private fun setupCommentRecyclerView() {
        commentAdapter = CommentAdapter(commentList) { commentId, writer, content ->
            navigateToCommentDetail(commentId, writer, content) // 클릭 이벤트 처리
        }
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = commentAdapter
    }

    private fun loadComments() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCommentsForBoard("Bearer $jwtToken", boardId)
                if (response.isSuccessful) {
                    val comments = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        commentList.clear()
                        commentList.addAll(comments)
                        commentAdapter.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("댓글을 불러오는 데 실패했습니다.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("댓글을 불러오는 중 오류가 발생했습니다.")
                }
            }
        }
    }

    private fun showAddCommentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_comment, null)
        val commentEditText = dialogView.findViewById<EditText>(R.id.commentEditText)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Comment")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val commentContent = commentEditText.text.toString()
                if (commentContent.isNotBlank()) {
                    createComment(commentContent)
                } else {
                    showToast("댓글 내용을 입력해주세요.")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun createComment(content: String) {
        val comment = CommentDto(
            id = null,
            boardId = boardId,
            content2 = content,
            writer = "Android User"
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.createComment("Bearer $jwtToken", boardId, comment)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("댓글이 성공적으로 추가되었습니다.")
                        loadComments()
                    } else {
                        showToast("댓글 추가 실패")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("댓글 추가 중 오류가 발생했습니다.")
                }
            }
        }
    }

    private fun navigateToCommentDetail(commentId: Long, writer: String, content: String) {
        val intent = Intent(this, CommentDetailActivity::class.java).apply {
            putExtra("COMMENT_ID", commentId)
            putExtra("COMMENT_WRITER", writer)
            putExtra("COMMENT_CONTENT", content)
        }
        startActivity(intent)
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.boardTitleEditText)
        val contentEditText = dialogView.findViewById<EditText>(R.id.boardContentEditText)
        val writerEditText = dialogView.findViewById<EditText>(R.id.boardWriterEditText)

        titleEditText.setText(titleTextView.text)
        contentEditText.setText(contentTextView.text)
        writerEditText.setText(writerTextView.text.toString().replace("작성자: ", ""))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Board")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()
                val writer = writerEditText.text.toString()

                if (title.isNotEmpty() && content.isNotEmpty() && writer.isNotEmpty()) {
                    updateBoard(title, content, writer)
                } else {
                    showToast("모든 필드를 입력해주세요.")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun updateBoard(title: String, content: String, writer: String) {
        val board = BoardDto(
            id = boardId,
            title = title,
            writer = writer,
            boardContent = content
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.updateBoard("Bearer $jwtToken", boardId, board)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("게시판이 성공적으로 수정되었습니다.")
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        showToast("게시판 수정 실패")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("게시판 수정 중 오류가 발생했습니다.")
                }
            }
        }
    }

    private fun deleteBoard() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteBoard("Bearer $jwtToken", boardId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("게시판이 성공적으로 삭제되었습니다.")
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        showToast("게시판 삭제 실패")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("게시판 삭제 중 오류가 발생했습니다.")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
