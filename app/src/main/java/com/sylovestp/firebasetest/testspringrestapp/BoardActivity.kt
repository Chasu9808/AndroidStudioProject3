package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
import com.sylovestp.firebasetest.testspringrestapp.dto.PageResponse
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class BoardActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var addBoardButton: Button
    private lateinit var updateBoardButton: Button
    private lateinit var deleteBoardButton: Button

    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var selectedBoardId: Long? = null // 선택된 게시글 ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        jwtToken = getJwtTokenFromSharedPreferences()
        if (jwtToken.isEmpty()) {
            Toast.makeText(this, "JWT 토큰이 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        recyclerView = findViewById(R.id.boardRecyclerView)
        addBoardButton = findViewById(R.id.addBoardButton)
        updateBoardButton = findViewById(R.id.updateBoardButton)
        deleteBoardButton = findViewById(R.id.deleteBoardButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        addBoardButton.setOnClickListener { showBoardDialog(null) }
        updateBoardButton.setOnClickListener {
            if (selectedBoardId == null) {
                Toast.makeText(this, "수정할 게시글을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                showBoardDialog(selectedBoardId)
            }
        }
        deleteBoardButton.setOnClickListener {
            if (selectedBoardId == null) {
                Toast.makeText(this, "삭제할 게시글을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                deleteBoard(selectedBoardId!!)
            }
        }

        fetchBoards()
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun fetchBoards() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getAllBoards(
                    token = "Bearer $jwtToken",
                    searchKeyword = null,
                    page = 0,
                    size = 10
                )
                Log.d("BoardActivity", "Response: ${response.body()}")
                handleFetchBoardsResponse(response)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BoardActivity, "게시판 데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun handleFetchBoardsResponse(response: Response<PageResponse<BoardDto>>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val boards = response.body()?.content ?: emptyList()
                Log.d("BoardActivity", "Boards: $boards")
                if (recyclerView.adapter == null) {
                    recyclerView.adapter = BoardAdapter(boards) { boardId ->
                        selectedBoardId = boardId
                        Toast.makeText(this@BoardActivity, "선택된 게시글 ID: $boardId", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    (recyclerView.adapter as BoardAdapter).updateBoards(boards)
                }
            } else {
                Toast.makeText(this@BoardActivity, "서버 응답 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBoardDialog(boardId: Long?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_board, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.boardTitleEditText)
        val contentEditText = dialogView.findViewById<EditText>(R.id.boardContentEditText)

        val dialogTitle = if (boardId == null) "Add Board" else "Update Board"
        val dialog = AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()

                if (boardId == null) {
                    createBoard(title, content)
                } else {
                    updateBoard(boardId, title, content)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun createBoard(title: String, content: String) {
        val board = BoardDto(
            title = title,
            writer = "Android User",
            boardContent = content
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.createBoard("Bearer $jwtToken", board)
                handleCreateOrUpdateResponse(response, "게시판이 성공적으로 추가되었습니다.")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BoardActivity, "게시판 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateBoard(boardId: Long, title: String, content: String) {
        val board = BoardDto(
            id = boardId,
            title = title,
            writer = "Android User",
            boardContent = content
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateBoard("Bearer $jwtToken", boardId, board)
                handleCreateOrUpdateResponse(response, "게시판이 성공적으로 수정되었습니다.")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BoardActivity, "게시판 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteBoard(boardId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteBoard("Bearer $jwtToken", boardId)
                handleDeleteResponse(response)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BoardActivity, "게시판 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun handleCreateOrUpdateResponse(response: Response<*>, successMessage: String) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                Toast.makeText(this@BoardActivity, successMessage, Toast.LENGTH_SHORT).show()
                fetchBoards()
            } else {
                Toast.makeText(this@BoardActivity, "요청 처리 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun handleDeleteResponse(response: Response<*>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                Toast.makeText(this@BoardActivity, "게시판이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                fetchBoards()
            } else {
                Toast.makeText(this@BoardActivity, "게시판 삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
