package com.sylovestp.firebasetest.testspringrestapp.dto

data class BoardDto(
    val id: Long? = null,
    val title: String,
    val writer: String,
    val boardContent: String,
    val filename: String? = null,
    val filepath: String? = null,
    val createDate: String? = null,
    val modifyDate: String? = null,
    val answerList: List<CommentDto>? = null
)
