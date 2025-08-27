package com.sylovestp.firebasetest.testspringrestapp.repository

import android.content.SharedPreferences
import com.sylovestp.firebasetest.testspringrestapp.dto.LoginRequest
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService

class LoginRepository(private val apiService: INetworkService, private val sharedPreferences: SharedPreferences) {

    suspend fun login(username: String, password: String): Boolean {
        val loginRequest = LoginRequest(username, password)
        val response = apiService.login(loginRequest)

        return if (response.isSuccessful && response.body() != null) {
            val accessToken = response.body()?.accessToken
            val refreshToken = response.body()?.refreshToken
            val username = response.body()?.username


            sharedPreferences.edit().putString("jwt_token", accessToken).apply()
            sharedPreferences.edit().putString("refreshToken", refreshToken).apply()
            sharedPreferences.edit().putString("username", username).apply()

            true
        } else {
            false
        }
    }
}