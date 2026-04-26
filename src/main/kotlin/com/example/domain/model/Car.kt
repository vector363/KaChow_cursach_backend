package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Car (
    val id: Int,
    val model: String,
    val brand: String,
    val  dealershipId: Int
)
