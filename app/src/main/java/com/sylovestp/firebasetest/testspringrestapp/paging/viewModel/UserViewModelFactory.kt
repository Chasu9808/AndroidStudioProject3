package com.sylovestp.firebasetest.testspringrestapp.paging.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class UserViewModelFactory(private val apiService: INetworkService) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}