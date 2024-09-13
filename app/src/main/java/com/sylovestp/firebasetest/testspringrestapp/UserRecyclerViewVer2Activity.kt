package com.sylovestp.firebasetest.testspringrestapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityUserRecyclerViewBinding
import com.sylovestp.firebasetest.testspringrestapp.databinding.ActivityUserRecyclerViewVer2Binding
import com.sylovestp.firebasetest.testspringrestapp.paging.adapter.UserAdapter
import com.sylovestp.firebasetest.testspringrestapp.paging.viewModel.UserViewModel
import com.sylovestp.firebasetest.testspringrestapp.paging.viewModel.UserViewModelFactory
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication

class UserRecyclerViewVer2Activity : AppCompatActivity() {
    private lateinit var apiService: INetworkService
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityUserRecyclerViewVer2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()

        viewModel = ViewModelProvider(this, UserViewModelFactory(apiService))
            .get(UserViewModel::class.java)

        adapter = UserAdapter()
        binding.retrofitRecyclerView2.layoutManager = LinearLayoutManager(this)
        binding.retrofitRecyclerView2.adapter = adapter

        viewModel.userPagingData.observe(this, { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        })

    }//onCreate

    }
