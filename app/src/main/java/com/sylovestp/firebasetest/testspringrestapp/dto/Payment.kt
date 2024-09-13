package com.sylovestp.firebasetest.testspringrestapp.dto

import java.time.LocalDateTime

data class Payment(
    val id: Long,
    val amount: Double,
    val paymentTime: LocalDateTime
)