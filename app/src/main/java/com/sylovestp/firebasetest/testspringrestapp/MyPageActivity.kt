package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sylovestp.firebasetest.testspringrestapp.dto.PasswordChangeRequest
import com.sylovestp.firebasetest.testspringrestapp.dto.UserDTO
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmNewPasswordEditText: EditText

    private val apiService by lazy { (application as MyApplication).networkService }
    private lateinit var jwtToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        initViews()
        jwtToken = getJwtTokenFromSharedPreferences()
        loadUserData()
    }

    private fun initViews() {
        usernameTextView = findViewById(R.id.usernameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        phoneTextView = findViewById(R.id.phoneTextView)
        addressTextView = findViewById(R.id.addressTextView)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        addressEditText = findViewById(R.id.addressEditText)
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText)
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun loadUserData() {
        if (jwtToken.isEmpty()) {
            showToast("JWT Token is missing. Please log in again.")
            return
        }

        val call = apiService.getMyPage("Bearer $jwtToken")
        call?.enqueue(object : Callback<UserDTO?> {
            override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    usernameTextView.text = user.username
                    emailTextView.text = user.email
                    phoneTextView.text = user.phone
                    addressTextView.text = user.address

                    emailEditText.setText(user.email)
                    phoneEditText.setText(user.phone)
                    addressEditText.setText(user.address)
                } else {
                    logError("Failed to load user data", response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                logError("Network error while loading user data", t.message)
            }
        })
    }

    fun onUpdateClick(view: View) {
        if (jwtToken.isEmpty()) {
            showToast("JWT Token is missing. Please log in again.")
            return
        }

        val updates = mapOf(
            "email" to emailEditText.text.toString(),
            "phone" to phoneEditText.text.toString(),
            "address" to addressEditText.text.toString()
        )

        val call = apiService.editUserField("Bearer $jwtToken", updates)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    showToast("User updated successfully")
                    loadUserData()
                } else {
                    logError("Failed to update user", response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                logError("Network error while updating user", t.message)
            }
        })
    }

    fun onChangePasswordClick(view: View) {
        val currentPassword = currentPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmNewPassword = confirmNewPasswordEditText.text.toString().trim()

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
            showToast("모든 필드를 입력해주세요.")
            return
        }

        if (newPassword != confirmNewPassword) {
            showToast("새 비밀번호가 일치하지 않습니다.")
            return
        }

        if (jwtToken.isEmpty()) {
            showToast("JWT Token is missing. Please log in again.")
            return
        }

        val passwordChangeRequest = PasswordChangeRequest(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        )

        val call = apiService.changePassword("Bearer $jwtToken", passwordChangeRequest)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    showToast("Password changed successfully")
                    clearSessionAndNavigateToMain()
                } else {
                    logError("Failed to change password", response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                logError("Network error while changing password", t.message)
            }
        })
    }

    fun onDeleteAccountClick(view: View) {
        val passwordDetails = mapOf(
            "password" to currentPasswordEditText.text.toString()
        )

        val call = apiService.deleteAccount("Bearer $jwtToken", passwordDetails)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    showToast("Account deleted successfully")
                    clearSessionAndNavigateToMain()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    logError("Failed to delete account", errorMessage)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                logError("Network error while deleting account", t.message)
            }
        })
    }

    private fun clearSessionAndNavigateToMain() {

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()


        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logError(message: String, error: String?) {
        Log.e("MyPageActivity", "$message: $error")
        showToast(message)
    }
}
