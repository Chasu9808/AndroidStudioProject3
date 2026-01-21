package com.sylovestp.firebasetest.testspringrestapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sylovestp.firebasetest.testspringrestapp.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginUser = MutableLiveData<String?>()
    val loginUser: LiveData<String?> get() = _loginUser

    fun login(username: String, password: String) {

        viewModelScope.launch {

            val loginSuccessful = loginRepository.login(username, password)

            if (loginSuccessful) {
                _loginUser.postValue(username)
            } else {
                _loginUser.postValue(null)
            }
        }
    }
}
