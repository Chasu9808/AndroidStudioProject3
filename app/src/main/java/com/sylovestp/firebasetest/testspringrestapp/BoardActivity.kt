package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.adapter.BoardAdapter
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BoardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addBoardButton: Button
    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        jwtToken = getJwtTokenFromSharedPreferences()
        if (jwtToken.isEmpty()) {
            showToast("JWT 토큰이 없습니다. 다시 로그인해주세요.")
            finish()
            return
        }

        initViews()
        fetchBoards()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.boardRecyclerView)
        addBoardButton = findViewById(R.id.addBoardButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        addBoardButton.setOnClickListener { showBoardDialog() }
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun fetchBoards() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getAllBoards(
                    token = "Bearer $jwtToken",
                    searchKeyword = null,
                    page = 0,
                    size = 10
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val boards = response.body()?.content ?: emptyList()
                        recyclerView.adapter = BoardAdapter(boards) { boardId ->
                            val selectedBoard = boards.find { it.id == boardId }
                            selectedBoard?.let { board ->
                                val intent = Intent(this@BoardActivity, BoardDetailActivity::class.java).apply {
                                    putExtra("boardId", board.id)
                                    putExtra("boardTitle", board.title)
                                    putExtra("boardWriter", board.writer)
                                    putExtra("boardContent", board.boardContent)
                                }
                                startActivityForResult(intent, REQUEST_CODE_DETAIL)
                            }
                        }
                    } else {
                        showToast("서버 응답 실패")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("게시판 데이터를 불러오는 중 오류가 발생했습니다.")
                    Log.e("BoardActivity", "fetchBoards Error: ${e.message}")
                }
            }
        }
    }

    private fun showBoardDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.boardTitleEditText)
        val contentEditText = dialogView.findViewById<EditText>(R.id.boardContentEditText)
        val writerEditText = dialogView.findViewById<EditText>(R.id.boardWriterEditText)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Board")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()
                val writer = writerEditText.text.toString()

                if (title.isNotEmpty() && content.isNotEmpty() && writer.isNotEmpty()) {
                    createBoard(title, content, writer)
                } else {
                    showToast("모든 필드를 입력해주세요.")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun createBoard(title: String, content: String, writer: String) {
        val board = BoardDto(
            title = title,
            writer = writer,
            boardContent = content
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.createBoard("Bearer $jwtToken", board)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("게시판이 성공적으로 추가되었습니다.")
                        fetchBoards()
                    } else {
                        showToast("게시판 추가 실패")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("게시판 추가 중 오류가 발생했습니다.")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == RESULT_OK) {
            fetchBoards() // 상세 페이지에서 돌아오면 게시글 목록 새로고침
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_DETAIL = 100
    }
}
