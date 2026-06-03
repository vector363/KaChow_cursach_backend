package com.example.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object CarImageTable : Table("car_images") {
    val id = integer("id").autoIncrement()
    val carId = integer("car_id").references(CarTable.id, onDelete = ReferenceOption.CASCADE)
    val imageUrl = varchar("image_url", 255)
    val isPreview = bool("is_preview").default(false)
    val orderIndex = integer("order_index").default(0)

    override val primaryKey = PrimaryKey(id)
}