package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class CreateBoardActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String
    private var boardId: Long? = null // 수정 시 사용할 게시글 ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        initViews()
        jwtToken = getJwtTokenFromSharedPreferences()

        // 수정 모드인지 확인
        boardId = intent.getLongExtra("BOARD_ID", -1L).takeIf { it != -1L }
        if (boardId != null) {
            // 수정 모드: 기존 게시글 데이터를 불러옵니다.
            fetchBoardDetails(boardId!!)
        }
    }

    private fun initViews() {
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        saveButton.setOnClickListener { saveBoard() }
        cancelButton.setOnClickListener { finish() }
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun fetchBoardDetails(boardId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getBoardById("Bearer $jwtToken", boardId)
                handleFetchBoardDetailsResponse(response)
            } catch (e: Exception) {
                logError("게시글 상세정보를 불러오는 중 오류 발생", e.message)
            }
        }
    }

    private suspend fun handleFetchBoardDetailsResponse(response: Response<BoardDto>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val board = response.body()
                board?.let {
                    // 제목과 내용을 UI에 채워넣습니다.
                    titleEditText.setText(it.title)
                    contentEditText.setText(it.boardContent)
                }
            } else {
                logError("게시글 상세정보를 불러오지 못했습니다.", response.errorBody()?.string())
            }
        }
    }

    private fun saveBoard() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            showToast("제목과 내용을 입력해주세요.")
            return
        }

        val boardDto = BoardDto(
            id = boardId,
            title = title,
            boardContent = content,
            writer = "Android User"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (boardId == null) {
                    // 새 글작성
                    apiService.createBoard("Bearer $jwtToken", boardDto)
                } else {
                    // 기존 글수정
                    apiService.updateBoard("Bearer $jwtToken", boardId!!, boardDto)
                }
                handleSaveBoardResponse(response)
            } catch (e: Exception) {
                logError("게시글 저장 중 오류 발생", e.message)
            }
        }
    }

    private suspend fun handleSaveBoardResponse(response: Response<ResponseBody>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                showToast(if (boardId == null) "게시글이 생성되었습니다." else "게시글이 수정되었습니다.")
                finish() // 작성/수정 완료 후 액티비티 종료
            } else {
                logError("게시글 저장 실패", response.errorBody()?.string())
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logError(message: String, error: String?) {
        Log.e("CreateBoardActivity", "$message: $error")
        showToast(message)
    }
}
