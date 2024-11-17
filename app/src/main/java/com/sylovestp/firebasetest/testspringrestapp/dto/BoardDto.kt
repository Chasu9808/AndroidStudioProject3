package com.sylovestp.firebasetest.testspringrestapp.dto

import java.time.LocalDateTime

data class BoardDto(
    val id: Long? = null,                  // 게시글 ID (nullable)
    val title: String,                    // 게시글 제목
    val writer: String,                   // 작성자
    val boardContent: String,             // 게시글 내용
    val filename: String? = null,         // 파일 이름 (nullable)
    val filepath: String? = null,         // 파일 경로 (nullable)
    val createDate: LocalDateTime? = null,// 생성일 (nullable)
    val modifyDate: LocalDateTime? = null,// 수정일 (nullable)
    val answerList: List<CommentDto>? = null // 댓글 리스트 (nullable)
)
