package com.example.domain.repository

import com.example.domain.model.Car

interface CarRepository {
    suspend fun getAllCar(): List<Car>
    suspend fun createCar(brand: String, model: String, dealershipId: Int): Car
}
