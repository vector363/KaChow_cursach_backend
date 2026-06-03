package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CarImage(
    val id: Int,
    val carId: Int,
    val imageUrl: String,
    val isPreview: Boolean,
    val orderIndex: Int
)