package com.sylovestp.firebasetest.testspringrestapp

import com.sylovestp.firebasetest.testspringrestapp.viewModel.LoginViewModel
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


        val savedUsername = sharedPreferences.getString("username", null)

        if (savedUsername != null) {

            showWelcomeMessage(binding, savedUsername)
            setupOptionsMenu(binding)
            binding.btnLogout.visibility = View.VISIBLE
            binding.btnSignUp.visibility = View.GONE
        } else {

            Toast.makeText(this, "로그인 후 옵션 메뉴를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            binding.btnLogout.visibility = View.GONE
        }


        binding.btnLogin.setOnClickListener {
            val username = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()

            loginViewModel.login(username, password)
        }


        loginViewModel.loginUser.observe(this) { username ->
            if (username != null) {
                sharedPreferences.edit().putString("username", username).apply()


                showWelcomeMessage(binding, username)
                setupOptionsMenu(binding)
                binding.btnLogout.visibility = View.VISIBLE
                binding.btnSignUp.visibility = View.GONE
            } else {
                Toast.makeText(this, "로그인을 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnLogout.setOnClickListener {

            sharedPreferences.edit().clear().apply()


            binding.tvWelcome.visibility = View.GONE
            binding.etId.visibility = View.VISIBLE
            binding.etPassword.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnSignUp.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE

            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        }


        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this@MainActivity, JoinActivity::class.java)
            startActivity(intent)
        }
    }


    private fun showWelcomeMessage(binding: ActivityMainBinding, username: String) {
        binding.tvWelcome.visibility = View.VISIBLE
        binding.tvWelcome.text = "$username 님, 환영합니다!"
        binding.etId.visibility = View.GONE
        binding.etPassword.visibility = View.GONE
        binding.btnLogin.visibility = View.GONE
        binding.btnSignUp.visibility = View.GONE
    }


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
                        R.id.action_board -> true
                        R.id.action_inquiry -> true
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
