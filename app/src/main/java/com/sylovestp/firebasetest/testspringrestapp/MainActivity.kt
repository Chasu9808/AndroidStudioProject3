package com.sylovestp.firebasetest.testspringrestapp

import LoginViewModel
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityMainBinding
import com.sylovestp.firebasetest.testspringrestapp.repository.LoginRepository
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication

import com.sylovestp.firebasetest.testspringrestapp.viewModelFactory.LoginViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var apiService: INetworkService
    private lateinit var sharedPreferences: SharedPreferences
    private val loginViewModel: LoginViewModel by viewModels {
        val loginRepository = LoginRepository(apiService, sharedPreferences)
        LoginViewModelFactory(loginRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // 앱 시작 시 로그인 상태 확인
        val savedUsername = sharedPreferences.getString("username", null)

        // 로그인된 경우에만 옵션 버튼 설정
        if (savedUsername != null) {
            // 로그인된 사용자 정보가 있으면 메뉴 설정
            showWelcomeMessage(binding, savedUsername)
            setupOptionsMenu(binding) // 옵션 메뉴 설정
        } else {
            // 로그인되지 않은 경우 메뉴가 사용되지 않음
            Toast.makeText(this, "로그인 후 옵션 메뉴를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
        }

        // 로그인 버튼 클릭 시 처리
        binding.btnLogin.setOnClickListener {
            val username = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()

            // 로그인 시도
            loginViewModel.login(username, password)
        }

        // 로그인 결과 관찰 (사용자 ID 확인)
        loginViewModel.loginUser.observe(this) { username ->
            if (username != null) {
                // 로그인 성공 시 SharedPreferences에 사용자 이름 저장
                sharedPreferences.edit().putString("username", username).apply()

                // 로그인 성공 시 메뉴바를 즉시 사용할 수 있도록 설정
                showWelcomeMessage(binding, username)
                setupOptionsMenu(binding) // 메뉴바 설정

                // 로그아웃 버튼 표시, 회원가입 버튼 숨기기
                binding.btnLogout.visibility = View.VISIBLE
                binding.btnSignUp.visibility = View.GONE
            } else {
                Toast.makeText(this, "로그인을 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 로그아웃 버튼 클릭 시 처리
        binding.btnLogout.setOnClickListener {
            // SharedPreferences에서 로그인 정보 삭제
            sharedPreferences.edit().clear().apply()

            // 로그인 화면 복원
            binding.tvWelcome.visibility = View.GONE
            binding.etId.visibility = View.VISIBLE
            binding.etPassword.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnSignUp.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE

            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

            sharedPreferences.edit().clear().apply()
        }

        // 회원가입 버튼 클릭 시 처리
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this@MainActivity, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    // 환영 메시지를 표시하고 로그인 관련 View를 숨기는 메서드
    private fun showWelcomeMessage(binding: ActivityMainBinding, username: String) {
        binding.tvWelcome.visibility = View.VISIBLE
        binding.tvWelcome.text = "$username 님, 환영합니다!"
        binding.etId.visibility = View.GONE
        binding.etPassword.visibility = View.GONE
        binding.btnLogin.visibility = View.GONE
        binding.btnSignUp.visibility = View.GONE
    }

    // 옵션 메뉴 설정
    private fun setupOptionsMenu(binding: ActivityMainBinding) {
        val btnOptions = findViewById<ImageButton>(R.id.btnOptions)
        if (btnOptions == null) {
            Log.e("MainActivity", "btnOptions is null")
        } else {
            btnOptions.setOnClickListener { view ->
                val popupMenu = PopupMenu(this, view)
                val inflater: MenuInflater = popupMenu.menuInflater
                inflater.inflate(R.menu.popup_menu, popupMenu.menu)
                popupMenu.show()

                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        R.id.action_image_classification -> {
                            val intent = Intent(this, AiPredictActivity::class.java)
                            startActivity(intent)
                            true
                        }
                        R.id.action_board -> true // 아직 구현되지 않음
                        R.id.action_inquiry -> true // 아직 구현되지 않음
                        R.id.action_my_page -> {
                            val intent = Intent(this, MyPageActivity::class.java)
                            startActivity(intent)
                            true
                        }
                        R.id.action_tool_list-> {
                            val intent = Intent(this, ToollistActivity::class.java)
                            startActivity(intent)
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }
}



