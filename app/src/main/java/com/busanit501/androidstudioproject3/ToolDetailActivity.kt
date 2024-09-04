package com.busanit501.androidstudioproject3

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class ToolDetailActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imgTextView: TextView
    private lateinit var regDateTextView: TextView
    private lateinit var modDateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tool_detail)

        nameTextView = findViewById(R.id.nameTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        imgTextView = findViewById(R.id.imgTextView)
        regDateTextView = findViewById(R.id.regDateTextView)
        modDateTextView = findViewById(R.id.modDateTextView)


        val toolName = intent.getStringExtra("tool_name")
        val toolDescription = intent.getStringExtra("tool_description")
        val imgText = intent.getStringExtra("img_text")
        val regDate = intent.getStringExtra("regDate")
        val modDate = intent.getStringExtra("modDate")


        nameTextView.text = toolName
        descriptionTextView.text = toolDescription
        imgTextView.text = imgText
        regDateTextView.text = regDate
        modDateTextView.text = modDate


    }
}
