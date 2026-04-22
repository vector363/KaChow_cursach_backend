package com.example.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String?,
    val passwordHash: String,
    val role: String,
    val createdAt: Long
)