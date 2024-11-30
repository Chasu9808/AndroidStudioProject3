package com.sylovestp.firebasetest.testspringrestapp.dto

data class CommentDto(
    val id: Long? = null,
    val boardId: Long,
    val content2: String,
    val writer: String? = null,
    val createDate: String? = null,
    val modifyDate: String? = null
)
