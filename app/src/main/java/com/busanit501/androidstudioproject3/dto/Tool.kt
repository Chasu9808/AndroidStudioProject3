package com.busanit501.androidstudioproject3.dto

import com.google.gson.annotations.SerializedName

data class Tool(
    val id: Long,

    @SerializedName("tool_name")
    val toolName: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("img_text")
    val imgText: String,

    @SerializedName("regDate")
    val regDate: String,

    @SerializedName("modDate")
    val modDate: String
)
