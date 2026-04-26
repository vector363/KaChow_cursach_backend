package com.example.data.repository


import com.example.data.database.tables.CarTable
import com.example.data.database.tables.FavoriteTable
import com.example.domain.model.Car
import com.example.domain.repository.FavoriteRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class FavoriteRepositoryImpl: FavoriteRepository {
    override suspend fun getUserFavorites(userId: Int): List<Car> = newSuspendedTransaction {
        (FavoriteTable innerJoin CarTable)
            .selectAll()
            .where { FavoriteTable.userId eq userId }
            .map { row ->
                Car(
                    id = row[CarTable.id],
                    brand = row[CarTable.brand],
                    model = row[CarTable.model],
                    dealershipId = row[CarTable.dealershipId]
                )
            }
    }

    override suspend fun addToFavorites(userId: Int, carId: Int) = newSuspendedTransaction  {
        FavoriteTable.insert{
            it[FavoriteTable.userId] = userId
            it[FavoriteTable.carId] = carId
        }
        Unit
    }

    override suspend fun removeFromFavorites(userId: Int, carId: Int) = newSuspendedTransaction {
        FavoriteTable.deleteWhere {
            (FavoriteTable.userId eq userId) and (FavoriteTable.carId eq carId)
        }
        Unit
    }

    override suspend fun isFavorite(userId: Int, carId: Int): Boolean = newSuspendedTransaction {
        FavoriteTable
            .selectAll()
            .where { (FavoriteTable.userId eq userId) and (FavoriteTable.carId eq carId) }
            .any()

    }
}