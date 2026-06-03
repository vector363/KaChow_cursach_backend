package com.example.domain.repository

import com.example.domain.model.CarImage

interface CarImageRepository {
    suspend fun getImagesByCar(carId: Int): List<CarImage>
    suspend fun addImage(carId: Int, imageUrl: String, isPreview: Boolean): CarImage
    suspend fun deleteImage(imageId: Int)
    suspend fun setPreview(carId: Int, imageId: Int)
}