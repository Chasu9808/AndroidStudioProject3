package com.sylovestp.firebasetest.testspringrestapp.dto

import com.google.gson.annotations.SerializedName

data class PageResponse<T>(
    @SerializedName("content") val content: List<T> = emptyList(),
    val totalPages: Int,
    val totalElements: Int,
)
