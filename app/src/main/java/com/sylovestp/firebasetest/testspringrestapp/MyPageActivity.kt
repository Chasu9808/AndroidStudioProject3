package com.sylovestp.firebasetest.testspringrestapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var deleteButton: Button
    private lateinit var myApplication: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // MyApplication 초기화
        myApplication = application as MyApplication

        // 버튼 초기화 및 클릭 이벤트 설정
        deleteButton = findViewById(R.id.button_delete_account)

        deleteButton.setOnClickListener {
            deleteAccount()  // 계정 삭제 함수 호출
        }
    }

    private fun deleteAccount() {
        // SharedPreferences에서 JWT 토큰 가져오기
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)  // 이미 저장된 JWT 토큰 불러오기

        Log.d("TokenRetrieve", "JWT 토큰 불러오기: $token")  // 토큰 값을 로그로 확인

        if (token != null) {
            val bearerToken = "Bearer $token"
            Log.d("BearerToken", "BearerToken 불러오기: $bearerToken")

            // Retrofit 요청 시작
            myApplication.getApiService().deleteAccount(bearerToken)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        // 요청 URL 및 헤더 로그 출력
                        Log.d("RetrofitRequest", "Request URL: ${call.request().url}")
                        Log.d("RetrofitRequest", "Request Headers: ${call.request().headers}")

                        // 응답이 성공적인지 확인
                        if (response.isSuccessful) {
                            Log.d("RetrofitResponse", "Response 성공: ${response.body()?.string()}")

                            // 계정 삭제 성공 후 SharedPreferences 초기화
                            val editor = sharedPreferences.edit()
                            editor.clear()  // 모든 데이터를 삭제
                            editor.apply()  // SharedPreferences 초기화 적용

                            Toast.makeText(
                                this@MyPageActivity,
                                "Account deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // 로그인 화면(MainActivity)로 이동하고 현재 액티비티 종료
                            val intent = Intent(this@MyPageActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // 이전 액티비티 스택 모두 제거
                            startActivity(intent)
                            finish()  // 현재 액티비티 종료
                        } else {
                            Log.e("RetrofitResponse", "Response 실패: ${response.errorBody()?.string()} - Code: ${response.code()}")

                            Toast.makeText(
                                this@MyPageActivity,
                                "Failed to delete account: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        // 요청 실패 시 로그 및 사용자에게 알림
                        Log.e("DeleteAccount", "Error: ${t.message}")
                        Toast.makeText(
                            this@MyPageActivity,
                            "Error deleting account",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "Token not found, please login again", Toast.LENGTH_SHORT).show()

            // 토큰이 없을 때 로그인 화면으로 이동
            val intent = Intent(this@MyPageActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
