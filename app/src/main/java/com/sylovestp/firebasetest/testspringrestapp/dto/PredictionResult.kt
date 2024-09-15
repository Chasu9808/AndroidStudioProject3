package com.sylovestp.firebasetest.testspringrestapp.dto

import com.google.gson.annotations.SerializedName

data class PredictionResult(
    //    @SerializedName("confidence") val confidence: Double,
//    @SerializedName("predicted_class_index") val predictedClassIndex: Int,
//    @SerializedName("predicted_class_label") val predictedClassLabel: String,
//    @SerializedName("class_confidences") val classConfidences: Map<String, Double>
//    @SerializedName("videoUrl") val videoUrl: String,
    @SerializedName("predictedLabel") val predictedLabel: String
)