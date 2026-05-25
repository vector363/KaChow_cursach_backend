package com.example.domain.repository

import com.example.domain.model.CarPreview

interface FavoriteRepository {
    suspend fun getUserFavorites(userId: Int): List<CarPreview>
    suspend fun addToFavorites(userId: Int, carId: Int)
    suspend fun removeFromFavorites(userId: Int, carId: Int)
    suspend fun isFavorite(userId: Int, carId: Int): Boolean
}

