package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Dealership (
    val id: Int,
    val name: String,
    val carCount: Int = 0
)
