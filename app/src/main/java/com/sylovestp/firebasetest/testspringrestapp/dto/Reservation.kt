package com.sylovestp.firebasetest.testspringrestapp.dto

import java.time.LocalDateTime

data class Reservation(
    val id: Long,
    val reservationTime: LocalDateTime,
    val numberOfGuests: Int,
    val payments: List<Payment>
)