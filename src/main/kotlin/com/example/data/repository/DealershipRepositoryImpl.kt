package com.example.data.repository

import com.example.data.database.tables.CarTable
import com.example.data.database.tables.DealershipTable
import com.example.domain.model.Dealership
import com.example.domain.repository.DealershipRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DealershipRepositoryImpl: DealershipRepository {
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
                carCount = counts[row[DealershipTable.id]] ?: 0
            )
        }
    }

    override suspend fun addDealership(name: String): Dealership = newSuspendedTransaction{
        val insertDealership = DealershipTable.insert{
            it[DealershipTable.name] = name
        }

        val dealershipId = insertDealership[DealershipTable.id]

        Dealership(
            id = dealershipId,
            name = name
        )
    }
}