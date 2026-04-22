package com.example.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val username: String,
    val email: String,
    val role: String
)