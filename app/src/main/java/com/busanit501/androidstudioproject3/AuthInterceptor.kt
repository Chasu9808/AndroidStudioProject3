package com.busanit501.androidstudioproject3

import com.busanit501.androidstudioproject3.App
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer " + (App.prefs.token ?: ""))
            .build()
        return chain.proceed(req)
    }
}