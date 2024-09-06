package com.busanit501.androidstudioproject3

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.busanit501.androidstudioproject3.adapter.ToolAdapter
import com.busanit501.androidstudioproject3.dto.Tool
import com.busanit501.androidstudioproject3.retrofit.RetrofitInstance
import okhttp3.*
import java.io.IOException
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolAdapter: ToolAdapter
    private var toolList: MutableList<Tool> = mutableListOf()

//    예시 더미 데이터
//    private lateinit var toolList: MutableList<Tool>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchToolsFromServer()

//        예시 더미 데이터
//        toolList = mutableListOf(
//            Tool("1", "Hammer", "A short description of Hammer", "A detailed description of Hammer"),
//            Tool("2", "Screwdriver", "A short description of Screwdriver", "A detailed description of Screwdriver")
//            // Add more tools as needed
//        )
//
//        toolAdapter = ToolAdapter(toolList, this)
//        recyclerView.adapter = toolAdapter

    }
    private fun fetchToolData() {
        val client = OkHttpClient()

        // API 엔드포인트를 지정
        val request = Request.Builder()
            .url("http://10.100.201.26:8080/api/tools") // 서버의 API 엔드포인트
            .build()

        // OkHttp의 Call 객체로 서버 요청
        client.newCall(request).enqueue(object : okhttp3.Callback { // okhttp3.Callback 사용
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // 서버 요청이 실패했을 때
                e.printStackTrace()
                Log.e("MainActivity", "Failure: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()

                    jsonData?.let {
                        val jsonArray = JSONArray(it)

                        // JSON 데이터를 파싱하여 Tool 객체 리스트로 변환
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            val tool = Tool(
                                id = jsonObject.getLong("id"),
                                toolName = jsonObject.getString("tool_name"),
                                description = jsonObject.getString("description"),
                                imgText = jsonObject.getString("img_text"),
                                regDate = jsonObject.getString("regDate"),
                                modDate = jsonObject.getString("modDate"),
                                imageFileName = jsonObject.getString("imageFileName")
                            )
                            toolList.add(tool) // 리스트에 추가
                        }

                        // UI 업데이트 (메인 스레드에서 실행)
                        runOnUiThread {
                            toolAdapter = ToolAdapter(toolList, this@MainActivity)
                            recyclerView.adapter = toolAdapter
                            toolAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code}")
                }
            }
        })
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
