package com.example.domain.model

import com.example.presentation.models.CarWithFavorite
import kotlinx.serialization.Serializable

@Serializable
data class Car (
    val id: Int,
    val model: String,
    val brand: String,
    val price: Int,
    val year: Int,
    val mileage: Int,
    val engine: String,
    val horsepower: Int,
    val transmission: String,
    val driveUnit: String,
    val color: String,
    val description: String,
    val imageUrl: String? = null,
    val  dealershipId: Int
)
