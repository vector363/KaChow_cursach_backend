package com.example.data.database.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 100).uniqueIndex()
    val email = varchar("email", 255).nullable()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50).default("user")
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}