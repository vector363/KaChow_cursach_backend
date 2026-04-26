package com.example.presentation.routes

import com.example.data.repository.FavoriteRepositoryImpl
import com.example.presentation.models.CarWithFavorite
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.favoriteRoutes() {
    val favoriteRepository = FavoriteRepositoryImpl()

    route("/favorites"){
        get{
            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "User not found"))
                return@get
            }
            val favorites = favoriteRepository.getUserFavorites(userId).map { car ->
                CarWithFavorite(
                    id = car.id,
                    brand = car.brand,
                    model = car.model,
                    dealershipId = car.dealershipId,
                    isFavorite = true
                )
            }
            call.respond(favorites)
        }

        post("/{carId}"){
            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "User not found"))
                return@post
            }

            val carId = call.parameters["carId"]?.toIntOrNull()

            if (carId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid car id"))
                return@post
            }

            favoriteRepository.addToFavorites(userId, carId)
            call.respond(HttpStatusCode.Created, mapOf("message" to "Added to favorites"))
        }

        delete("/{carId}"){
            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt() ?: return@delete

            val carId = call.parameters["carId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid car id")

            favoriteRepository.removeFromFavorites(userId, carId)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Removed from favorites"))
        }
    }
}