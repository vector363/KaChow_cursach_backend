package com.example.data.repository

import com.example.data.database.tables.CarImageTable
import com.example.data.database.tables.CarTable
import com.example.domain.model.CarImage
import com.example.domain.repository.CarImageRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class CarImageRepositoryImpl : CarImageRepository {

    override suspend fun getImagesByCar(carId: Int): List<CarImage> = newSuspendedTransaction {
        CarImageTable
            .selectAll()
            .where { CarImageTable.carId eq carId }
            .orderBy(CarImageTable.orderIndex to SortOrder.ASC)
            .map { row ->
                CarImage(
                    id = row[CarImageTable.id],
                    carId = row[CarImageTable.carId],
                    imageUrl = row[CarImageTable.imageUrl],
                    isPreview = row[CarImageTable.isPreview],
                    orderIndex = row[CarImageTable.orderIndex]
                )
            }
    }

    override suspend fun addImage(carId: Int, imageUrl: String, isPreview: Boolean): CarImage = newSuspendedTransaction {
        val maxOrderResult = CarImageTable
            .selectAll()
            .where { CarImageTable.carId eq carId }
            .orderBy(CarImageTable.orderIndex to SortOrder.DESC)
            .limit(1)
            .map { it[CarImageTable.orderIndex] }
            .firstOrNull()

        val newOrder = (maxOrderResult ?: -1) + 1

        val insertResult = CarImageTable.insert { it ->
            it[CarImageTable.carId] = carId
            it[CarImageTable.imageUrl] = imageUrl
            it[CarImageTable.isPreview] = isPreview
            it[CarImageTable.orderIndex] = newOrder
        }

        val imageId = insertResult[CarImageTable.id]

        if (newOrder == 0 || isPreview) {
            CarTable.update({ CarTable.id eq carId }) {
                it[CarTable.imageUrl] = imageUrl
            }
        }

        if (isPreview || newOrder == 0) {
            setPreview(carId, imageId)
        }

        CarImage(
            id = imageId,
            carId = carId,
            imageUrl = imageUrl,
            isPreview = isPreview,
            orderIndex = newOrder
        )
    }

    override suspend fun deleteImage(imageId: Int) = newSuspendedTransaction {
        val image = CarImageTable
            .selectAll()
            .where { CarImageTable.id eq imageId }
            .map {
                it[CarImageTable.carId] to it[CarImageTable.imageUrl]
            }
            .firstOrNull()

        CarImageTable.deleteWhere { CarImageTable.id eq imageId }

        image?.let { (carId, deletedImageUrl) ->
            val remainingImages = CarImageTable
                .selectAll()
                .where { CarImageTable.carId eq carId }
                .orderBy(CarImageTable.orderIndex to SortOrder.ASC)
                .map { it[CarImageTable.imageUrl] }

            if (remainingImages.isNotEmpty()) {
                CarTable.update({ CarTable.id eq carId }) {
                    it[CarTable.imageUrl] = remainingImages.first()
                }
            } else {
                CarTable.update({ CarTable.id eq carId }) {
                    it[CarTable.imageUrl] = null
                }
            }
        }
        Unit
    }

    override suspend fun setPreview(carId: Int, imageId: Int) = newSuspendedTransaction {
        CarImageTable.update({ CarImageTable.carId eq carId }) { it ->
            it[CarImageTable.isPreview] = false
        }

        CarImageTable.update({ CarImageTable.id eq imageId }) { it ->
            it[CarImageTable.isPreview] = true
        }

        val previewImageUrl = CarImageTable
            .selectAll()
            .where { CarImageTable.id eq imageId }
            .map { it[CarImageTable.imageUrl] }
            .singleOrNull()

        previewImageUrl?.let {
            CarTable.update({ CarTable.id eq carId }) {
                it[CarTable.imageUrl] = previewImageUrl
            }
        }
        Unit
    }
}