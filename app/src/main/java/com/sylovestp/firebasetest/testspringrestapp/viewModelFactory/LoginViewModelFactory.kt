package com.sylovestp.firebasetest.testspringrestapp.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sylovestp.firebasetest.testspringrestapp.repository.LoginRepository
import com.sylovestp.firebasetest.testspringrestapp.viewModel.LoginViewModel

class LoginViewModelFactory(
    private val loginRepository: LoginRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}