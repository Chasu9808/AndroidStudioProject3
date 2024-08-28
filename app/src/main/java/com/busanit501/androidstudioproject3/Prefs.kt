package com.busanit501.androidstudioproject3

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log

class Prefs(context: Context) {
    private val prefNm = "mPref"
    private val prefs = context.getSharedPreferences(prefNm, MODE_PRIVATE)

    // Prefs.kt에 로그 추가
    var token: String?
        get() {
            val storedToken = prefs.getString("token", null)
            Log.d("JWT_TOKEN", "Stored token: $storedToken") // 로그 출력
            return storedToken
        }
        set(value) {
            Log.d("JWT_TOKEN", "New token: $value") // 로그 출력
            prefs.edit().putString("token", value).apply()
        }
}