package com.sylovestp.firebasetest.testspringrestapp.dto

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)
