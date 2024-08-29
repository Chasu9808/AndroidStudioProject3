package com.busanit501.androidstudioproject3.lhj


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit501.androidlabtest501.databinding.ActivityMain1Binding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMain1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 버튼 클릭 시 로그인 액티비티로 이동
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 회원가입 버튼 클릭 시 회원가입 액티비티로 이동
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}