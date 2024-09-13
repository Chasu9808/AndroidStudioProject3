package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit501.androidlabtest501.R
import com.sylovestp.firebasetest.testspringrestapp.dto.Tool
import com.sylovestp.firebasetest.testspringrestapp.adapter.ToolAdapter
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToollistActivity : AppCompatActivity() {

    private lateinit var toolAdapter: ToolAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var myApplication: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toollist)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        myApplication = application as MyApplication


        if (isNetworkAvailable(this)) {
            fetchToolsFromServer()
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchToolsFromServer() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        if (token != null) {
            val bearerToken = "Bearer $token"


            myApplication.getApiService().findAll(bearerToken)
                .enqueue(object : Callback<List<Tool>> {
                    override fun onResponse(call: Call<List<Tool>>, response: Response<List<Tool>>) {
                        if (response.isSuccessful) {
                            val toolList = response.body() ?: emptyList()
                            toolAdapter = ToolAdapter(toolList, this@ToollistActivity)
                            recyclerView.adapter = toolAdapter
                        } else {
                            handleApiError(response)
                        }
                    }

                    override fun onFailure(call: Call<List<Tool>>, t: Throwable) {
                        Log.e("ToollistActivity", "Error: ${t.message}")
                        Toast.makeText(this@ToollistActivity, "Error fetching tools: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            handleMissingToken()
        }
    }


    private fun handleApiError(response: Response<*>) {
        when (response.code()) {
            401 -> {
                Toast.makeText(this, "Unauthorized access. Please login again.", Toast.LENGTH_SHORT).show()

                redirectToLogin()
            }
            500 -> {
                Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.e("ToollistActivity", "Response 실패: ${response.errorBody()?.string()} - Code: ${response.code()}")
                Toast.makeText(this, "Failed to fetch tools: ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun handleMissingToken() {
        Toast.makeText(this, "Token not found, please login again", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }


    private fun redirectToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
