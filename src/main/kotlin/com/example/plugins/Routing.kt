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

val userRepository: UserRepository = UserRepositoryImpl()

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

                val user = if (userId != null) {
                    userRepository.findById(userId)
                } else {
                    null
                }

                call.respond(
                    UserResponse(
                        userId = user?.id,
                        username = user?.username,
                        role = user?.role,
                        email = user?.email,
                        createdAt = user?.createdAt
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