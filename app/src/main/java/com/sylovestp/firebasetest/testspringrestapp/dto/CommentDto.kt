package com.sylovestp.firebasetest.testspringrestapp.dto

import java.time.LocalDateTime

data class CommentDto(
    val id: Long? = null,                  // 댓글 ID (nullable)
    val boardId: Long,                    // 게시글 ID
    val content: String,                  // 댓글 내용
    val createDate: LocalDateTime? = null,// 댓글 생성일 (nullable)
    val modifyDate: LocalDateTime? = null // 댓글 수정일 (nullable)
)
