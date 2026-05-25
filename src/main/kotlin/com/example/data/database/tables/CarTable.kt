package com.example.data.database.tables

import org.jetbrains.exposed.sql.Table


object CarTable : Table("car") {
    val id = integer("id").autoIncrement()
    val brand = varchar("brand", 50)
    val model = varchar("model", 100)
    val price = integer("price")
    val year = integer("year")
    val mileage = integer("mileage")
    val engine = varchar("engine", 50)
    val horsepower = integer("horsepower")
    val transmission = varchar("transmission", 50)
    val driveUnit = varchar("drive_unit", 50)
    val color = varchar("color", 30)
    val description = text("description")
    val imageUrl = varchar("image_url", 255).nullable()
    val dealershipId = integer("dealership_id").references(DealershipTable.id)

    override val primaryKey = PrimaryKey(id)
}
