package com.sylovestp.firebasetest.testspringrestapp.dto

data class UserItem(
    val id: Long,
    val username: String,
    val name: String,
    val email: String,
    val password: String,
//    val reservations: List<Reservation>,
    val profileImageId: String,
    val phone: String,
    val address: String,
//    val roleSet: List<String>
)