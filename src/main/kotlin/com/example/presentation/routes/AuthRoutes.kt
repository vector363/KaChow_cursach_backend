package com.example.presentation.routes

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
import com.example.data.repository.UserRepositoryImpl



fun Route.authRoutes() {
    val userRepository = UserRepositoryImpl()

    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            val existingUser = userRepository.findByUsername(request.username)
            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to "User name already exists"))
                return@post
            }

            val existingEmail = userRepository.findByEmail(request.email)
            if (existingEmail != null) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already exists"))
                return@post
            }

            val passwordHash = PasswordHasher.hash(request.password)

            val user = userRepository.createUser(
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

            val user = userRepository.findByUsername(request.username)
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