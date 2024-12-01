package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
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
        updateBoardButton = findViewById(R.id.updateBoardButton)
        deleteBoardButton = findViewById(R.id.deleteBoardButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        addBoardButton.setOnClickListener { showBoardDialog(null) }
        updateBoardButton.setOnClickListener {
            if (selectedBoardId == null) {
                showToast("수정할 게시글을 선택해주세요.")
            } else {
                showBoardDialog(selectedBoardId)
            }
        }
        deleteBoardButton.setOnClickListener {
            if (selectedBoardId == null) {
                showToast("삭제할 게시글을 선택해주세요.")
            } else {
                deleteBoard(selectedBoardId!!)
            }
        }
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
                        Log.d("BoardActivity", "Raw JSON: ${response.body()}")
                        Log.d("BoardActivity", "Response Error Body: ${response.errorBody()?.string()}")
                        Log.d("BoardActivity", "Response Code: ${response.code()}")
                        Log.d("BoardActivity", "Response Body: ${response.body()}")
                        val boards = response.body()?.content ?: emptyList()
                        if (boards.isEmpty()) {
                            showToast("게시글이 없습니다.")
                            Log.d("BoardActivity", "Boards are empty or null")
                        }
                        (recyclerView.adapter as? BoardAdapter)?.updateBoards(boards) ?: run {
                            recyclerView.adapter = BoardAdapter(boards) { boardId ->
                                selectedBoardId = boardId
                                showToast("선택된 게시글 ID: $boardId")
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

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.createBoard("Bearer $jwtToken", board)
                handleCreateOrUpdateResponse(response, "게시판이 성공적으로 추가되었습니다.")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("게시판 추가 중 오류가 발생했습니다.")
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

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.updateBoard("Bearer $jwtToken", boardId, board)
                handleCreateOrUpdateResponse(response, "게시판이 성공적으로 수정되었습니다.")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("게시판 수정 중 오류가 발생했습니다.")
                }
            }
        }
    }

    private fun deleteBoard(boardId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteBoard("Bearer $jwtToken", boardId)
                handleDeleteResponse(response)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("게시판 삭제 중 오류가 발생했습니다.")
                }
            }
        }
    }

    private suspend fun handleCreateOrUpdateResponse(response: Response<*>, successMessage: String) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                showToast(successMessage)
                fetchBoards()
            } else {
                showToast("요청 처리 실패")
            }
        }
    }

    private suspend fun handleDeleteResponse(response: Response<*>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                showToast("게시판이 성공적으로 삭제되었습니다.")
                fetchBoards()
            } else {
                showToast("게시판 삭제 실패")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
