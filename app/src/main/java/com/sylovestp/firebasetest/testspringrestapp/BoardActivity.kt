package com.sylovestp.firebasetest.testspringrestapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto
import com.sylovestp.firebasetest.testspringrestapp.dto.PageResponse
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity : AppCompatActivity() {

    private val apiService = MyApplication().getApiService()
    private lateinit var recyclerView: RecyclerView
    private lateinit var boardAdapter: BoardAdapter
    private val boardList = mutableListOf<BoardDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // RecyclerView 설정
        recyclerView = findViewById(R.id.rvBoardList)
        boardAdapter = BoardAdapter(boardList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@BoardActivity)
            adapter = boardAdapter
        }

        // 게시판 목록 가져오기
        loadBoardList()
    }

    private fun loadBoardList() {
        val token = "Bearer your_jwt_token" // 실제 JWT 토큰으로 대체해야 합니다
        apiService.getAllBoards(token, null, 0, 10).enqueue(object : Callback<PageResponse<BoardDto>> {
            override fun onResponse(call: Call<PageResponse<BoardDto>>, response: Response<PageResponse<BoardDto>>) {
                if (response.isSuccessful) {
                    val boardListResponse = response.body()?.content
                    if (boardListResponse != null) {
                        boardList.clear()
                        boardList.addAll(boardListResponse)
                        boardAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@BoardActivity, "No boards available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@BoardActivity, "Failed to load board list", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PageResponse<BoardDto>>, t: Throwable) {
                Toast.makeText(this@BoardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
