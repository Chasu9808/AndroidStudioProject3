package com.sylovestp.firebasetest.testspringrestapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
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

        // Fragment를 추가할 컨테이너가 존재하는지 확인 (예: R.id.fragment_container)
//        if (savedInstanceState == null) {
//            val fragment = ItemFragment()
//            supportFragmentManager.beginTransaction()
//                .add(R.id.fragment_container, fragment)  // R.id.fragment_container는 activity_main.xml에 정의된 컨테이너의 ID입니다.
//                .commit()
//        }

        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()

        val userListCall = apiService.getItems(0,10)

        userListCall.enqueue(object : Callback<PageResponse<UserItem>> {
            //익명 클래스가, Callback , 레트로핏2에서 제공하는 인터페이스를 구현했고,
            // 반드시 재정의해야하는 함수들이 있음.
            override fun onResponse(call: Call<PageResponse<UserItem>>, response: Response<PageResponse<UserItem>>) {
                // 데이터를 성공적으로 받았을 때 수행되는 함수
                val userList = response.body()?.content
                Log.d("lsy","userList의 값 : ${userList}")

                // 데이터를 성공적으로 받았다면, 여기서 리사이클러 뷰 어댑터에 연결하면 됨.
                // 리사이클러뷰 의 레이아웃 정하는 부분, 기본인 LinearLayoutManager 이용했고,
                val layoutManager = LinearLayoutManager(
                    this@UserRecyclerViewActivity)
                // 리사이클러뷰에 어댑터 연결
                // 인자값은 : 현재 context : this@HttpTestReqResActivity
                // 2번째 인자값은 : 데이터 , 네트워크 ,레트로핏2 통신으로 받아온 데이터 리스트
                binding.retrofitRecyclerView.layoutManager = layoutManager
                binding.retrofitRecyclerView.adapter =
                    MyAdapterRetrofit(this@UserRecyclerViewActivity,userList)

            }

            override fun onFailure(call: Call<PageResponse<UserItem>>, t: Throwable) {

                // 데이터를 못 받았을 때 수행되는 함수
                call.cancel()
            }

        })


    }//onCreate

}