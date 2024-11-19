package com.sylovestp.firebasetest.testspringrestapp.dto

import java.time.LocalDateTime

data class CommentDto(
    val id: Long? = null,
    val boardId: Long,
    val content: String,
    val writer: String? = null,           // 작성자 필드 추가
    val createDate: LocalDateTime? = null,
    val modifyDate: LocalDateTime? = null
)
