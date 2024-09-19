package com.sylovestp.firebasetest.testspringrestapp.dto

import com.google.gson.annotations.SerializedName

data class PredictionResult(

    @SerializedName("predictedLabel")
    val predictedLabel: String,
    val description: String,    // 설명 추가
    val videoUrl: String        // YouTube URL 추가
)