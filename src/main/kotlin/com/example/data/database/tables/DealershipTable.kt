package com.example.data.database.tables

import org.jetbrains.exposed.sql.Table

object DealershipTable : Table("dealership") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30).uniqueIndex()
    val address = varchar("address", 255)
    val rating = varchar("rating", 3)
    val imageUrl = varchar("image_url", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}