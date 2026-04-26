package com.example.data.database.tables

import org.jetbrains.exposed.sql.Table

object FavoriteTable : Table("favorite") {
    val userId = integer("user_id").references(UsersTable.id)
    val carId = integer("car_id").references(CarTable.id)
    override val primaryKey = PrimaryKey(userId, carId)
}