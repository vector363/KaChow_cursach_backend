package com.example.domain.repository

import com.example.domain.model.Car

interface FavoriteRepository {
    suspend fun getUserFavorites(userId: Int): List<Car>
    suspend fun addToFavorites(userId: Int, carId: Int)
    suspend fun removeFromFavorites(userId: Int, carId: Int)
    suspend fun isFavorite(userId: Int, carId: Int): Boolean
}

