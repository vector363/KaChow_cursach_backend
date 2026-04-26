package com.example.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class CarWithFavorite(
    val id: Int,
    val brand: String,
    val model: String,
    val dealershipId: Int,
    val isFavorite: Boolean
)