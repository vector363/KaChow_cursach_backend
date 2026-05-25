package com.example.plugins

import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.UserRepository
import com.example.presentation.models.UserResponse
import com.example.presentation.routes.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("server is running!")
        }
        staticRoutes()

        authRoutes()

        authenticate {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(com.example.presentation.models.UserResponse(
                        userId = null,
                        username = null,
                        role = null,
                        email = null,
                        createdAt = null
                    ))
                    return@get
                }

                val userRepository = UserRepositoryImpl()
                val user = userRepository.findById(userId)

                if (user == null) {
                    call.respond(com.example.presentation.models.UserResponse(
                        userId = null,
                        username = null,
                        role = null,
                        email = null,
                        createdAt = null
                    ))
                    return@get
                }

                call.respond(
                    UserResponse(
                        userId = user.id,
                        username = user.username,
                        role = user.role,
                        email = user.email,
                        createdAt = user.createdAt
                    )
                )
            }
            carRoutes()
            dealershipRoutes()
            favoriteRoutes()
            imageRoutes()
        }
    }
}