package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardPageResponse
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class BoardActivity : AppCompatActivity() {

    private lateinit var boardListTextView: TextView
    private lateinit var boardTitleEditText: EditText
    private lateinit var boardContentEditText: EditText
    private lateinit var addBoardButton: Button
    private lateinit var updateBoardButton: Button
    private lateinit var deleteBoardButton: Button

    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        initViews()
        jwtToken = getJwtTokenFromSharedPreferences()
        if (jwtToken.isEmpty()) {
            showToast("JWT 토큰이 없습니다. 다시 로그인해주세요.")
            navigateToLogin()
            return
        }
        fetchBoards()
    }

    private fun initViews() {
        boardListTextView = findViewById(R.id.boardListTextView)
        boardTitleEditText = findViewById(R.id.boardTitleEditText)
        boardContentEditText = findViewById(R.id.boardContentEditText)
        addBoardButton = findViewById(R.id.addBoardButton)
        updateBoardButton = findViewById(R.id.updateBoardButton)
        deleteBoardButton = findViewById(R.id.deleteBoardButton)

        addBoardButton.setOnClickListener { createBoard() }
        updateBoardButton.setOnClickListener { updateBoardPrompt() }
        deleteBoardButton.setOnClickListener { deleteBoardPrompt() }
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun fetchBoards() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getAllBoards("Bearer $jwtToken", null, 0, 10)
                handleFetchBoardsResponse(response)
            } catch (e: Exception) {
                logError("게시판 데이터를 불러오는 중 오류 발생", e.message)
            }
        }
    }

    private suspend fun handleFetchBoardsResponse(response: Response<BoardPageResponse>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val boardPage = response.body()
                if (boardPage != null && boardPage.boards.isNotEmpty()) {
                    val boardTexts = boardPage.boards.map { board ->
                        "제목: ${board.title}, 내용: ${board.boardContent}, 작성자: ${board.writer}"
                    }
                    boardListTextView.text = boardTexts.joinToString("\n")
                } else {
                    boardListTextView.text = "게시글이 없습니다."
                }
            } else {
                logError("게시판 데이터를 불러오지 못했습니다.", response.errorBody()?.string())
            }
        }
    }

    private fun createBoard() {
        val title = boardTitleEditText.text.toString()
        val content = boardContentEditText.text.toString()

        if (title.isBlank() || content.isBlank()) {
            showToast("제목과 내용을 입력해주세요.")
            return
        }

        val board = BoardDto(title = title, boardContent = content, writer = "Android User")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.createBoard("Bearer $jwtToken", board)
                handleCreateOrUpdateResponse(response, "게시글이 성공적으로 생성되었습니다.")
            } catch (e: Exception) {
                logError("게시글 생성 중 오류 발생", e.message)
            }
        }
    }

    private fun updateBoardPrompt() {
        val boardId = getBoardIdFromInput()
        if (boardId != null) {
            updateBoard(boardId)
        } else {
            showToast("유효한 게시글 ID를 입력해주세요.")
        }
    }

    private fun updateBoard(boardId: Long) {
        val title = boardTitleEditText.text.toString()
        val content = boardContentEditText.text.toString()

        if (title.isBlank() || content.isBlank()) {
            showToast("제목과 내용을 입력해주세요.")
            return
        }

        val board = BoardDto(id = boardId, title = title, boardContent = content, writer = "Android User")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateBoard("Bearer $jwtToken", boardId, board)
                handleCreateOrUpdateResponse(response, "게시글이 성공적으로 수정되었습니다.")
            } catch (e: Exception) {
                logError("게시글 수정 중 오류 발생", e.message)
            }
        }
    }

    private fun deleteBoardPrompt() {
        val boardId = getBoardIdFromInput()
        if (boardId != null) {
            deleteBoard(boardId)
        } else {
            showToast("유효한 게시글 ID를 입력해주세요.")
        }
    }

    private fun deleteBoard(boardId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteBoard("Bearer $jwtToken", boardId)
                handleDeleteResponse(response)
            } catch (e: Exception) {
                logError("게시글 삭제 중 오류 발생", e.message)
            }
        }
    }

    private suspend fun handleCreateOrUpdateResponse(response: Response<*>, successMessage: String) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                showToast(successMessage)
                fetchBoards()
            } else {
                logError("요청 처리 실패", response.errorBody()?.string())
            }
        }
    }

    private suspend fun handleDeleteResponse(response: Response<*>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                showToast("게시글이 성공적으로 삭제되었습니다.")
                fetchBoards()
            } else {
                logError("게시글 삭제 실패", response.errorBody()?.string())
            }
        }
    }

    private fun getBoardIdFromInput(): Long? {
        val input = boardTitleEditText.text.toString()
        return input.toLongOrNull()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logError(message: String, error: String?) {
        Log.e("BoardActivity", "$message: $error")
        showToast(message)
    }
}
