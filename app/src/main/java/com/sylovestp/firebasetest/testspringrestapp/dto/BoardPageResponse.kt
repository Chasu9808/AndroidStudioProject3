package com.sylovestp.firebasetest.testspringrestapp.dto

data class BoardPageResponse(
    val boards: List<BoardDto>, // 서버에서 받은 게시글 리스트
    val totalPages: Int,        // 전체 페이지 수
    val totalElements: Long     // 전체 게시글 수
)
