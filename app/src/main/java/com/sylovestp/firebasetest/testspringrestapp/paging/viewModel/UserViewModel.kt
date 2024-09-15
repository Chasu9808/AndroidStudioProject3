package com.sylovestp.firebasetest.testspringrestapp.paging.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem
import com.sylovestp.firebasetest.testspringrestapp.paging.UserPagingSource
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class UserViewModel(private val apiService: INetworkService) : ViewModel() {

    val userPagingData: LiveData<PagingData<UserItem>> = Pager(
        config = PagingConfig(
            pageSize = 10, // 한 페이지에 가져올 아이템 수
            enablePlaceholders = false
        ),
        pagingSourceFactory = { UserPagingSource(apiService) }
    ).liveData
}
