package com.sylovestp.firebasetest.testspringrestapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit501.androidlabtest501.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityUserRecyclerViewBinding
import com.sylovestp.firebasetest.testspringrestapp.dto.PageResponse
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import com.sylovestp.firebasetest.testspringrestapp.paging.adapter.MyAdapterRetrofit
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRecyclerViewActivity : AppCompatActivity() {
    private lateinit var apiService: INetworkService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityUserRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()

        val userListCall = apiService.getItems(0,10)

        userListCall.enqueue(object : Callback<PageResponse<UserItem>> {

            override fun onResponse(call: Call<PageResponse<UserItem>>, response: Response<PageResponse<UserItem>>) {

                val userList = response.body()?.content
                Log.d("lsy","userList의 값 : ${userList}")


                val layoutManager = LinearLayoutManager(
                    this@UserRecyclerViewActivity)

                binding.retrofitRecyclerView.layoutManager = layoutManager
                binding.retrofitRecyclerView.adapter =
                    MyAdapterRetrofit(this@UserRecyclerViewActivity,userList)

            }

            override fun onFailure(call: Call<PageResponse<UserItem>>, t: Throwable) {


                call.cancel()
            }

        })


    }

}