package com.sylovestp.firebasetest.testspringrestapp.interceptor

import android.content.SharedPreferences
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.math.log

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // SharedPreferences에서 토큰을 가져옴
        val token = sharedPreferences.getString("jwt_token", "")
        Log.d("AuthorizationInterceptor", "JWT Token retrieved: $token")

        // 토큰이 존재하고, 빈 문자열이 아닌 경우에만 Authorization 헤더 추가
        val newRequest = if (!token.isNullOrEmpty()) {
            Log.d("AuthorizationInterceptor", "Adding Authorization Header with Token: Bearer $token")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.w("AuthorizationInterceptor", "Token is null or empty, skipping header addition")
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
