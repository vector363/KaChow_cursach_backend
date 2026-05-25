package com.example.data.repository

import com.example.data.database.tables.CarTable
import com.example.data.database.tables.DealershipTable
import com.example.domain.model.Dealership
import com.example.domain.repository.DealershipRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class DealershipRepositoryImpl : DealershipRepository {

    override suspend fun getAllDealership(): List<Dealership> = newSuspendedTransaction {
        val dealerships = DealershipTable.selectAll().toList()
        val counts = mutableMapOf<Int, Int>()

        CarTable.selectAll().forEach { car ->
            val dealershipId = car[CarTable.dealershipId]
            counts[dealershipId] = (counts[dealershipId] ?: 0) + 1
        }

        dealerships.map { row ->
            Dealership(
                id = row[DealershipTable.id],
                name = row[DealershipTable.name],
                address = row[DealershipTable.address],
                rating = row[DealershipTable.rating],
                carCount = counts[row[DealershipTable.id]] ?: 0,
                imageUrl = row[DealershipTable.imageUrl]
            )
        }
    }

    override suspend fun addDealership(name: String, address: String, rating: String): Dealership = newSuspendedTransaction {
        val insertDealership = DealershipTable.insert {
            it[DealershipTable.name] = name
            it[DealershipTable.address] = address
            it[DealershipTable.rating] = rating
        }

        val dealershipId = insertDealership[DealershipTable.id]

        Dealership(
            id = dealershipId,
            name = name,
            address = address,
            rating = rating,
            carCount = 0,
            imageUrl = null
        )
    }

    override suspend fun updateImage(dealershipId: Int, imageUrl: String?): Dealership? = newSuspendedTransaction {
        DealershipTable.update({ DealershipTable.id eq dealershipId }) {
            it[DealershipTable.imageUrl] = imageUrl
        }

        val updated = DealershipTable
            .selectAll()
            .where { DealershipTable.id eq dealershipId }
            .map { row ->
                Dealership(
                    id = row[DealershipTable.id],
                    name = row[DealershipTable.name],
                    address = row[DealershipTable.address],
                    rating = row[DealershipTable.rating],
                    carCount = 0,
                    imageUrl = row[DealershipTable.imageUrl]
                )
            }
            .singleOrNull()

        return@newSuspendedTransaction updated
    }
}