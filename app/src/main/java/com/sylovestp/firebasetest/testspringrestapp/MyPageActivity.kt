package com.sylovestp.firebasetest.testspringrestapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private val apiService = MyApplication().getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        // 예: 사용자 정보 가져오기
        val token = "Bearer your_jwt_token"
        apiService.getMyPage(token).enqueue(object : Callback<UserItem> {
            override fun onResponse(call: Call<UserItem>, response: Response<UserItem>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    // user 정보를 UI에 표시
                } else {
                    Toast.makeText(this@MyPageActivity, "Failed to load user info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserItem>, t: Throwable) {
                Toast.makeText(this@MyPageActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
