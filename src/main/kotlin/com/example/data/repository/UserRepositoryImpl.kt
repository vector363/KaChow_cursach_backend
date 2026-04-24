package com.example.data.repository

import com.example.data.database.tables.UsersTable
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class UserRepositoryImpl : UserRepository {
    override suspend fun findByUsername(username: String): User? = newSuspendedTransaction {
        UsersTable
            .selectAll()
            .where { UsersTable.username eq username }
            .map {
                User(
                    id = it[UsersTable.id],
                    username = it[UsersTable.username],
                    email = it[UsersTable.email],
                    passwordHash = it[UsersTable.passwordHash],
                    role = it[UsersTable.role],
                    createdAt = it[UsersTable.createdAt]
                )
            }
            .singleOrNull()
    }

    override suspend fun createUser(username: String, email: String, passwordHash: String): User = newSuspendedTransaction {
        UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.role] = "user"
            it[UsersTable.createdAt] = System.currentTimeMillis()
        }

        val createdUser = UsersTable
            .selectAll()
            .where { UsersTable.username eq username }
            .map {
                User(
                    id = it[UsersTable.id],
                    username = it[UsersTable.username],
                    email = it[UsersTable.email],
                    passwordHash = it[UsersTable.passwordHash],
                    role = it[UsersTable.role],
                    createdAt = it[UsersTable.createdAt]
                )
            }
            .singleOrNull() ?: throw Exception("Failed to retrieve created user")

        return@newSuspendedTransaction createdUser
    }

    override suspend fun findById(id: Int): User? = newSuspendedTransaction {
        UsersTable
            .selectAll()
            .where { UsersTable.id eq id }
            .map {
                User(
                    id = it[UsersTable.id],
                    username = it[UsersTable.username],
                    email = it[UsersTable.email],
                    passwordHash = it[UsersTable.passwordHash],
                    role = it[UsersTable.role],
                    createdAt = it[UsersTable.createdAt]
                )
            }
            .singleOrNull()
    }

    override suspend fun findByEmail(email: String): User? = newSuspendedTransaction{
        UsersTable
            .selectAll()
            .where { UsersTable.email eq email }
            .map {
                User(
                    id = it[UsersTable.id],
                    username = it[UsersTable.username],
                    email = it[UsersTable.email],
                    passwordHash = it[UsersTable.passwordHash],
                    role = it[UsersTable.role],
                    createdAt = it[UsersTable.createdAt]
                )
            }
            .singleOrNull()
    }
}