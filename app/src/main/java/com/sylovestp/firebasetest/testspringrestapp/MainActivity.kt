package com.sylovestp.firebasetest.testspringrestapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //버튼 요소 선택하기. 로그인 이동하기
        binding.ch06LsyLoginBtn.setOnClickListener {
            //로그인 버튼 클릭시, 로그인 페이지 이동하기.
            val intent = Intent(this,LoginActivity::class.java)
            // 메세지 전달하기.
            startActivity(intent)
        }

        //버튼 요소 선택하기. 회원가입 이동하기
        binding.ch06LsyJoinBtn.setOnClickListener {
            //회원가입 버튼 클릭시, 회원가입 페이지 이동하기.
            val intent = Intent(this,JoinActivity::class.java)
            // 메세지 전달하기.
            startActivity(intent)
        }

        binding.ch06LsyTestPagingBtn.setOnClickListener {
            val intent = Intent(this,UserRecyclerViewActivity::class.java)
            startActivity(intent)
        }

        binding.ch06LsyTestPagingBtn2.setOnClickListener {
            val intent = Intent(this,UserRecyclerViewVer2Activity::class.java)
            startActivity(intent)
        }

        // 버튼 클릭, 페이지 이동.
        binding.ch06LsyAiPredictBtn.setOnClickListener {
            val intent = Intent(this,AiPredictActivity::class.java)
            startActivity(intent)
        }

        binding.ch06LsyPayBtn.setOnClickListener {
            //회원가입 버튼 클릭시, 회원가입 페이지 이동하기.
            val intent = Intent(this,PayTestActivity::class.java)
            // 메세지 전달하기.
            startActivity(intent)
        }


    } //oncreate
}