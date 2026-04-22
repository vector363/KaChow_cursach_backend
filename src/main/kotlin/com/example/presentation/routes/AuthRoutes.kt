package com.example.presentation.routes

import com.example.data.database.InMemoryUserStorage
import com.example.domain.usecase.LoginUseCase
import com.example.presentation.models.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.presentation.models.AuthResponse
import com.example.presentation.models.RegisterRequest
import com.example.security.JwtConfig
import com.example.security.PasswordHasher


fun Route.authRoutes() {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            val existingUser = InMemoryUserStorage.findByUsername(request.username)
            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to "Username already exists"))
                return@post
            }

            val passwordHash = PasswordHasher.hash(request.password)

            val user = InMemoryUserStorage.createUser(
                username = request.username,
                email = request.email,
                passwordHash = passwordHash
            )

            val token = JwtConfig.generateToken(user.id, user.username, user.role)

            call.respond(
                HttpStatusCode.Created,
                AuthResponse(
                    token = token,
                    userId = user.id,
                    username = user.username,
                    email = user.email ?: "",
                    role = user.role
                )
            )
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val user = InMemoryUserStorage.findByUsername(request.username)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
                return@post
            }

            if (!PasswordHasher.verify(request.password, user.passwordHash)) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid username or password"))
                return@post
            }

            val token = JwtConfig.generateToken(user.id, user.username, user.role)

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    token = token,
                    userId = user.id,
                    username = user.username,
                    email = user.email ?: "",
                    role = user.role
                )
            )
        }
    }
}