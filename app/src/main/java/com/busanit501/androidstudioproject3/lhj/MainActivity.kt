package com.busanit501.androidstudioproject3.lhj

import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.busanit501.androidlabtest501.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)  // activity_main1.xml 레이아웃 사용

        // 옵션 버튼 클릭 시 팝업 메뉴 표시
        findViewById<ImageButton>(R.id.btnOptions).setOnClickListener { view ->  // ImageButton으로 수정
            val popupMenu = PopupMenu(this, view)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.show()

            // 팝업 메뉴 항목 클릭 리스너 설정
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.action_image_classification -> {
                        // ImageClassification 액티비티로 이동
                        val intent = Intent(this, ImageClassification::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.action_board -> {
                        // 게시판 액티비티로 이동 (아직 구현되지 않음)
                        // val intent = Intent(this, BoardActivity::class.java)
                        // startActivity(intent)
                        true
                    }
                    R.id.action_inquiry -> {
                        // 문의사항 액티비티로 이동 (아직 구현되지 않음)
                        // val intent = Intent(this, InquiryActivity::class.java)
                        // startActivity(intent)
                        true
                    }
                    R.id.action_my_page -> {
                        // MyPage 액티비티로 이동
                        val intent = Intent(this, MyPageActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }

        // 로그인 버튼 클릭 시 처리
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)  // LoginActivity로 이동
            startActivity(intent)
        }

        // 회원가입 버튼 클릭 시 처리
        findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)  // SignUpActivity로 이동
            startActivity(intent)
        }
    }
}