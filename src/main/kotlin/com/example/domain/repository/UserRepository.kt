package com.example.domain.repository

import com.example.domain.model.User

interface UserRepository {
    suspend fun findByUsername(username: String): User?
    suspend fun createUser(username: String, email: String, passwordHash: String): User
    suspend fun findById(id: Int): User?
    suspend fun findByEmail(email: String): User?
}