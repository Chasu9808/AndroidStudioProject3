package com.busanit501.androidstudioproject3

import android.util.Log
import com.busanit501.androidstudioproject3.App
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = App.prefs.token
        Log.d("JWT_TOKEN", "Using token: $token for request: ${chain.request().url}")  // 로그 추가
        val req = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(req)
    }
}