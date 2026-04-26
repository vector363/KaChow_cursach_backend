package com.example.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class AddCarRequest(
    val brand: String,
    val model: String,
    val dealershipId: Int
)