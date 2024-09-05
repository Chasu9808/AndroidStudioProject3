package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityMainBinding
import com.sylovestp.firebasetest.testspringrestapp.repository.LoginRepository
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import com.sylovestp.firebasetest.testspringrestapp.viewModel.LoginViewModel
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

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 옵션 버튼 클릭 시 팝업 메뉴 표시
        findViewById<ImageButton>(R.id.btnOptions).setOnClickListener { view ->
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
                    R.id.action_my_page -> true // 아직 구현되지 않음
                    else -> false
                }
            }
        }

        // 로그인 버튼 클릭 시 처리
        binding.btnLogin.setOnClickListener {
            val username = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()

            loginViewModel.login(username, password)
        }

        // 회원가입 버튼 클릭 시 처리
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this@MainActivity, JoinActivity::class.java)
            startActivity(intent)
        }

        // 로그인 결과 관찰
        loginViewModel.loginResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AiPredictActivity::class.java)) // 로그인 성공 시 다음 화면으로 이동
                finish()
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show() // 로그인 실패 시 메시지
            }
        }
    }
}