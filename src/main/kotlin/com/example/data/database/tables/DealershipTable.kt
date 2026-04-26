package com.example.data.database.tables

import org.jetbrains.exposed.sql.Table

object DealershipTable : Table("dealership") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30).uniqueIndex()
    //val address = varchar("address", 100).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}