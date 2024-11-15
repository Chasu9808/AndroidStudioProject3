package com.sylovestp.firebasetest.testspringrestapp.dto

import java.time.LocalDateTime

data class CommentDto(
    val id: Long?,
    val boardId: Long?,
    val content2: String?,
    val createDate: LocalDateTime?,
    val modifyDate: LocalDateTime?
)
