package com.busanit501.androidstudioproject3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit501.androidstudioproject3.adapter.ToolAdapter
import com.busanit501.androidstudioproject3.dto.Tool

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolAdapter: ToolAdapter
    private lateinit var toolList: MutableList<Tool>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        toolList = mutableListOf(
            Tool("1", "Hammer", "A short description of Hammer", "A detailed description of Hammer"),
            Tool("2", "Screwdriver", "A short description of Screwdriver", "A detailed description of Screwdriver")
            // Add more tools as needed
        )

        toolAdapter = ToolAdapter(toolList, this)
        recyclerView.adapter = toolAdapter

    }
}
