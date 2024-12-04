package com.sylovestp.firebasetest.testspringrestapp.dto

import com.google.gson.annotations.SerializedName

data class PredictionResult(

    @SerializedName("predictedLabel")
    val predictedLabel: String,
    val description: String,
    val videoUrl: String
)