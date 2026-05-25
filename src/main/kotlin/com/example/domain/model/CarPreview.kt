package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CarPreview(
    val id: Int,
    val brand: String,
    val model: String,
    val price: Int,
    val year: Int,
    val mileage: Int,
    val imageUrl: String?,
    val dealershipId: Int,
    val isFavorite: Boolean
)