package com.example.domain.repository

import com.example.domain.model.Dealership

interface DealershipRepository {
    suspend fun getAllDealership(): List<Dealership>
    suspend fun addDealership(name: String, address: String, rating: String): Dealership
    suspend fun updateImage(dealershipId: Int, imageUrl: String?): Dealership?
}
