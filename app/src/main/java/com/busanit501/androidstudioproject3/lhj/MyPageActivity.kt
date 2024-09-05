package com.busanit501.androidstudioproject3.lhj


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MyPageActivity : AppCompatActivity() {

    private lateinit var deleteAccountButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.busanit501.androidlabtest501.R.layout.activity_my_page)

        // 회원 탈퇴 버튼 초기화
        deleteAccountButton = findViewById(com.busanit501.androidlabtest501.R.id.deleteAccountButton)

        // 회원 탈퇴 버튼 클릭 이벤트 처리
        deleteAccountButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    // 회원 탈퇴 확인 다이얼로그 표시
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Yes") { _, _ -> deleteAccount() }
            .setNegativeButton("No", null)
            .show()
    }

    // 회원 탈퇴 처리
    private fun deleteAccount() {
        // 여기서 서버에 회원 탈퇴 요청을 보냅니다.
        // 예를 들어, Retrofit 또는 다른 네트워킹 라이브러리를 사용하여 요청을 보낼 수 있습니다.

        // 이 예제에서는 성공적으로 탈퇴한 것으로 간주하고 토스트 메시지를 표시합니다.
        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()

        // 탈퇴 후 로그인 화면 또는 초기 화면으로 이동합니다.
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}