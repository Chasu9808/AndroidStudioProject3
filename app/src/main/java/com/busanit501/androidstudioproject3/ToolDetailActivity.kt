package com.busanit501.androidstudioproject3

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ToolDetailActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tool_detail)
        nameTextView = findViewById(R.id.nameTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)

        val toolName = intent.getStringExtra("tool_name")
        val toolDescription = intent.getStringExtra("tool_description")

        nameTextView.text = toolName
        descriptionTextView.text = toolDescription

    }
}
