package com.sylovestp.firebasetest.testspringrestapp.dto

import java.time.LocalDateTime

data class BoardDto(
    val id: Long?,
    val title: String?,
    val writer: String?,
    val boardContent: String?,
    val filename: String?,
    val filepath: String?,
    val createDate: LocalDateTime?,
    val modifyDate: LocalDateTime?,
    val answerList: List<CommentDto>? // CommentDto를 리스트로 포함
)
