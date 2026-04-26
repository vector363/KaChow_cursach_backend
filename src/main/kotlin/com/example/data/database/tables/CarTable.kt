package com.example.data.database.tables

import org.jetbrains.exposed.sql.Table


object CarTable: Table("car") {
    val id = integer("id").autoIncrement()
    val brand = varchar("brand", 30)
    val model = varchar("model", 30)
    val dealershipId = integer("dealership_id").references(DealershipTable.id)

    override val primaryKey = PrimaryKey(id)
}
