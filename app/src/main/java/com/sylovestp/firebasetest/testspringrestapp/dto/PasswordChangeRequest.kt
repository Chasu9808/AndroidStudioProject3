package com.sylovestp.firebasetest.testspringrestapp.dto

data class PasswordChangeRequest(
    val currentPassword: String, // 기존 비밀번호
    val newPassword: String,      // 새 비밀번호
    val confirmNewPassword: String
)
