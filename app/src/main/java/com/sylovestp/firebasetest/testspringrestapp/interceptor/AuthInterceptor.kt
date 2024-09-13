package com.sylovestp.firebasetest.testspringrestapp.interceptor

import android.content.SharedPreferences
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.math.log

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()


        val token = sharedPreferences.getString("jwt_token", "")
        Log.d("AuthorizationInterceptor", "JWT Token retrieved: $token")


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
