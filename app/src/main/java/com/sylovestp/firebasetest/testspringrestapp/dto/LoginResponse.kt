package com.sylovestp.firebasetest.testspringrestapp.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
)