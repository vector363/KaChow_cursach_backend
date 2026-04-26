package com.example.data.database

import com.example.data.database.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://ep-red-grass-anprvmu7.c-6.us-east-1.aws.neon.tech:5432/neondb?sslmode=require&channel_binding=require"
            username = "neondb_owner"
            password = "npg_0OpVfUetZF4l"
            driverClassName = "org.postgresql.Driver"


            maximumPoolSize = 10
            minimumIdle = 2
            connectionTimeout = 30000
            maxLifetime = 1800000
            idleTimeout = 300000

            addDataSourceProperty("socketTimeout", "30")
            addDataSourceProperty("tcpKeepAlive", "true")
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.drop(CarTable)
            SchemaUtils.drop(DealershipTable)

            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                DealershipTable,
                CarTable
            )
        }
    }
}