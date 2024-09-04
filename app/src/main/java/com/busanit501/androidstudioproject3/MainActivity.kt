package com.busanit501.androidstudioproject3

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit501.androidstudioproject3.adapter.ToolAdapter
import com.busanit501.androidstudioproject3.dto.Tool
import com.busanit501.androidstudioproject3.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolAdapter: ToolAdapter
//    private lateinit var toolList: MutableList<Tool>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchToolsFromServer()

//        toolList = mutableListOf(
//            Tool("1", "Hammer", "A short description of Hammer", "A detailed description of Hammer"),
//            Tool("2", "Screwdriver", "A short description of Screwdriver", "A detailed description of Screwdriver")
//            // Add more tools as needed
//        )
//
//        toolAdapter = ToolAdapter(toolList, this)
//        recyclerView.adapter = toolAdapter

    }

    private fun fetchToolsFromServer() {
        RetrofitInstance.api.findAll().enqueue(object : Callback<List<Tool>> {
            override fun onResponse(call: Call<List<Tool>>, response: Response<List<Tool>>) {
                if (response.isSuccessful) {
                    val toolList = response.body() ?: emptyList()
                    toolAdapter = ToolAdapter(toolList, this@MainActivity)
                    recyclerView.adapter = toolAdapter
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Tool>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })

    }
}
