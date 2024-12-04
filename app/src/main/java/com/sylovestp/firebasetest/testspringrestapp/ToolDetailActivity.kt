package com.sylovestp.firebasetest.testspringrestapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity



class ToolDetailActivity : AppCompatActivity() {

    private lateinit var toolImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imgTextView: TextView
    private lateinit var regDateTextView: TextView
    private lateinit var modDateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tool_detail)

        toolImageView = findViewById(R.id.toolImageView)
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

        val imageResId = when (toolName) {
            "망치" -> R.drawable.hammer
            "니퍼" -> R.drawable.nipper
            "줄자" -> R.drawable.tape_measure
            "그라인더" -> R.drawable.grinder
            "드라이버" -> R.drawable.screwdriver
            "전동드릴" -> R.drawable.drill
            "스패너" -> R.drawable.spanner
            "공업가위" -> R.drawable.scissors
            "톱" -> R.drawable.saw
            "캘리퍼스" -> R.drawable.vernier_calipers
            else -> R.drawable.ic_launcher_foreground
        }


        toolImageView.setImageResource(imageResId)


    }
}
