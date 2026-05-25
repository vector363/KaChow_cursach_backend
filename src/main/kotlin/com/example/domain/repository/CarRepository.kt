package com.example.domain.repository

import com.example.domain.model.Car
import com.example.domain.model.CarPreview

interface CarRepository {
    suspend fun getAllCar(): List<Car>
    suspend fun getCarById(id: Int): Car?
    suspend fun getCarsByDealership(dealershipId: Int, userId: Int): List<CarPreview>
    suspend fun updateCar( id: Int,
                           brand: String,
                           model: String,
                           price: Int,
                           year: Int,
                           mileage: Int,
                           engine: String,
                           horsepower: Int,
                           transmission: String,
                           driveUnit: String,
                           color: String,
                           description: String,
                           imageUrl: String?,
                           dealershipId: Int): Car
    suspend fun createCar( brand: String,
                           model: String,
                           price: Int,
                           year: Int,
                           mileage: Int,
                           engine: String,
                           horsepower: Int,
                           transmission: String,
                           driveUnit: String,
                           color: String,
                           description: String,
                           imageUrl: String?,
                           dealershipId: Int): Car
    suspend fun updatePreviewImage(carId: Int, imageUrl: String?): Car?
}
