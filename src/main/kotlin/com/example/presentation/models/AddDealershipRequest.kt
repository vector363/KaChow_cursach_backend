package com.example.presentation.models

import kotlinx.serialization.Serializable

@Serializable
data class AddDealershipRequest(
    val name: String
)