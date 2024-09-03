package com.busanit501.androidstudioproject3.lhj

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.busanit501.androidlabtest501.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val username = findViewById<EditText>(R.id.username).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()

            if (validateLogin(username, password)) {
                // 로그인 성공 시 ImageClassification으로 이동
                val intent = Intent(this, ImageClassification::class.java)
                startActivity(intent)
                finish()
            } else {
                // 로그인 실패 시 처리 (예: 경고 메시지 표시)
            }
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // 로그인 검증 로직 추가
        return username.isNotEmpty() && password.isNotEmpty()
    }
}
