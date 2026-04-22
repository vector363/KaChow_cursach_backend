package com.example.presentation.routes

import com.example.domain.usecase.LoginUseCase
import com.example.presentation.models.LoginRequest
import com.example.presentation.models.LoginResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.usecase.GetUserUseCase
import com.example.presentation.models.RegisterRequest
import com.example.security.JwtConfig
import com.example.security.PasswordHasher


fun Route.authRoutes() {
    val userRepository = UserRepositoryImpl()
    val loginUseCase = LoginUseCase(userRepository)
    val getUserUseCase = GetUserUseCase(userRepository)

    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val token = loginUseCase.execute(request.username, request.password)

            if (token != null) {
                val user = loginUseCase.getUser(request.username)
                call.respond(
                    HttpStatusCode.OK,
                    LoginResponse(
                        token = token,
                        userId = user?.id ?: 0,
                        username = user?.username ?: "",
                        email = user?.email ?: ""
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Invalid username or password")
                )
            }
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()

            val existingUser = loginUseCase.getUser(request.username)
            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to "User already exists"))
                return@post
            }

            val passwordHash = PasswordHasher.hash(request.password)
            val newUser = userRepository.createUser(
                username = request.username,
                email = request.email,
                passwordHash = passwordHash
            )

            val token = JwtConfig.generateToken(newUser.id, newUser.username, newUser.role)

            call.respond(
                HttpStatusCode.Created,
                LoginResponse(
                    token = token,
                    userId = newUser.id,
                    username = newUser.username,
                    email = newUser.email ?: ""
                )
            )
        }
    }
}