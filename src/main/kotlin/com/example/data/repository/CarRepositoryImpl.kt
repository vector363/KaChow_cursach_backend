package com.example.data.repository

import com.example.data.database.tables.CarTable
import com.example.domain.model.Car
import com.example.domain.repository.CarRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class CarRepositoryImpl: CarRepository {

    override suspend fun getAllCar(): List<Car> = newSuspendedTransaction {
        CarTable
            .selectAll()
            .map { rowTocar(it) }
    }

    override suspend fun createCar(brand: String, model: String, dealershipId: Int): Car = newSuspendedTransaction{
        val insertResult = CarTable.insert {
            it[CarTable.brand] = brand
            it[CarTable.model] = model
            it[CarTable.dealershipId] = dealershipId
        }

        val carId = insertResult[CarTable.id]

        Car(
            id = carId,
            brand = brand,
            model = model,
            dealershipId = dealershipId
        )
    }

    private fun rowTocar(row: ResultRow): Car {
        return Car(
            id = row[CarTable.id],
            brand = row[CarTable.brand],
            model = row[CarTable.model],
            dealershipId = row[CarTable.dealershipId]
        )
    }
}