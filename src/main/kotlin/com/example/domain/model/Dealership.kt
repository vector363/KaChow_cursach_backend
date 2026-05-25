package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Dealership(
    val id: Int,
    val name: String,
    val address: String,
    val rating: String = "0",
    val carCount: Int = 0,
    val imageUrl: String? = null
)
