package com.example.plugins

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

        authRoutes()

        authenticate {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val username = principal?.payload?.getClaim("username")?.asString()
                val role = principal?.payload?.getClaim("role")?.asString()

                call.respond(
                    UserResponse(
                        userId = userId,
                        username = username,
                        role = role
                    )
                )
            }
            carRoutes()
            dealershipRoutes()
            favoriteRoutes()
        }
    }
}