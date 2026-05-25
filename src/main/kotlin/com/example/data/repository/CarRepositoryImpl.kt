package com.example.data.repository

import com.example.data.database.tables.CarTable
import com.example.data.database.tables.FavoriteTable
import com.example.domain.model.Car
import com.example.domain.model.CarPreview
import com.example.domain.repository.CarRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class CarRepositoryImpl: CarRepository {

    override suspend fun getAllCar(): List<Car> = newSuspendedTransaction {
        CarTable
            .selectAll()
            .map { rowToCar(it) }
    }

    override suspend fun createCar(
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
        dealershipId: Int): Car = newSuspendedTransaction{

        val insertResult = CarTable.insert {
            it[CarTable.brand] = brand
            it[CarTable.model] = model
            it[CarTable.price] = price
            it[CarTable.year] = year
            it[CarTable.mileage] = mileage
            it[CarTable.engine] = engine
            it[CarTable.horsepower] = horsepower
            it[CarTable.transmission] = transmission
            it[CarTable.driveUnit] = driveUnit
            it[CarTable.color] = color
            it[CarTable.description] = description
            it[CarTable.imageUrl] = imageUrl
            it[CarTable.dealershipId] = dealershipId
        }
        val carId = insertResult[CarTable.id]

        Car(
            id = carId,
            brand = brand,
            model = model,
            price = price,
            year = year,
            mileage = mileage,
            engine = engine,
            horsepower = horsepower,
            transmission = transmission,
            driveUnit = driveUnit,
            color = color,
            description = description,
            imageUrl = imageUrl,
            dealershipId = dealershipId
        )
    }

    override suspend fun getCarById(id: Int): Car? = newSuspendedTransaction {
        CarTable
            .selectAll()
            .where { CarTable.id eq id }
            .map {

                Car(
                    id = it[CarTable.id],
                    brand = it[CarTable.brand],
                    model = it[CarTable.model],
                    dealershipId = it[CarTable.dealershipId],
                    price = it[CarTable.price],
                    year = it[CarTable.year],
                    mileage = it[CarTable.mileage],
                    engine = it[CarTable.engine],
                    horsepower = it[CarTable.horsepower],
                    transmission = it[CarTable.transmission],
                    driveUnit = it[CarTable.driveUnit],
                    color = it[CarTable.color],
                    description = it[CarTable.description],
                    imageUrl = it[CarTable.imageUrl],
                )
            }
            .singleOrNull()
    }

    override suspend fun getCarsByDealership(dealershipId: Int, userId: Int): List<CarPreview> = newSuspendedTransaction {
        CarTable
            .selectAll()
            .where { CarTable.dealershipId eq dealershipId }
            .map { row ->
                val carId = row[CarTable.id]

                val isFav = FavoriteTable
                    .selectAll()
                    .where { (FavoriteTable.userId eq userId) and (FavoriteTable.carId eq carId) }
                    .any()

                CarPreview(
                    id = carId,
                    brand = row[CarTable.brand],
                    model = row[CarTable.model],
                    price = row[CarTable.price],
                    year = row[CarTable.year],
                    mileage = row[CarTable.mileage],
                    imageUrl = row[CarTable.imageUrl],
                    dealershipId = row[CarTable.dealershipId],
                    isFavorite = isFav
                )
            }
    }

    override suspend fun updateCar(
        id: Int,
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
        dealershipId: Int): Car = newSuspendedTransaction {
        val existingCar = CarTable.selectAll().where { CarTable.id eq id }.singleOrNull()
            ?: throw NoSuchElementException("Car with id $id not found")

        CarTable.update({ CarTable.id eq id }) {
            it[CarTable.brand] = brand
            it[CarTable.model] = model
            it[CarTable.price] = price
            it[CarTable.year] = year
            it[CarTable.mileage] = mileage
            it[CarTable.engine] = engine
            it[CarTable.horsepower] = horsepower
            it[CarTable.transmission] = transmission
            it[CarTable.driveUnit] = driveUnit
            it[CarTable.color] = color
            it[CarTable.description] = description
            it[CarTable.imageUrl] = imageUrl
            it[CarTable.dealershipId] = dealershipId
        }


        Car(
            id = id,
            brand = brand,
            model = model,
            price = price,
            year = year,
            mileage = mileage,
            engine = engine,
            horsepower = horsepower,
            transmission = transmission,
            driveUnit = driveUnit,
            color = color,
            description = description,
            imageUrl = imageUrl,
            dealershipId = dealershipId
        )
    }

    private fun rowToCar(row: ResultRow): Car {
        return Car(
            id = row[CarTable.id],
            brand = row[CarTable.brand],
            model = row[CarTable.model],
            price = row[CarTable.price],
            year = row[CarTable.year],
            mileage = row[CarTable.mileage],
            engine = row[CarTable.engine],
            horsepower = row[CarTable.horsepower],
            transmission = row[CarTable.transmission],
            driveUnit = row[CarTable.driveUnit],
            color = row[CarTable.color],
            description = row[CarTable.description],
            imageUrl = row[CarTable.imageUrl],
            dealershipId = row[CarTable.dealershipId]
        )
    }

    override suspend fun updatePreviewImage(carId: Int, imageUrl: String?): Car? = newSuspendedTransaction {
        CarTable.update({ CarTable.id eq carId }) {
            it[CarTable.imageUrl] = imageUrl
        }

        CarTable
            .selectAll()
            .where { CarTable.id eq carId }
            .map { rowToCar(it) }
            .singleOrNull()
    }
}