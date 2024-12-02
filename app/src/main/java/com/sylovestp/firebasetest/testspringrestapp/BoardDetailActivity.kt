package com.sylovestp.firebasetest.testspringrestapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
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
    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var boardId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)

        jwtToken = getJwtTokenFromSharedPreferences()

        boardId = intent.getLongExtra("boardId", -1)
        val boardTitle = intent.getStringExtra("boardTitle")
        val boardWriter = intent.getStringExtra("boardWriter")
        val boardContent = intent.getStringExtra("boardContent")

        titleTextView = findViewById(R.id.detailBoardTitleTextView)
        writerTextView = findViewById(R.id.detailBoardWriterTextView)
        contentTextView = findViewById(R.id.detailBoardContentTextView)
        editButton = findViewById(R.id.editBoardButton)
        deleteButton = findViewById(R.id.deleteBoardButton)

        titleTextView.text = boardTitle
        writerTextView.text = "작성자: $boardWriter"
        contentTextView.text = boardContent

        editButton.setOnClickListener { showEditDialog() }
        deleteButton.setOnClickListener { deleteBoard() }
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
                val response = apiService.updateBoard("Bearer $jwtToken", boardId!!, board)
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
                val response = apiService.deleteBoard("Bearer $jwtToken", boardId!!)
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
