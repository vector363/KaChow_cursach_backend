package com.example.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val userId: Int?,
    val username: String?,
    val role: String?
)