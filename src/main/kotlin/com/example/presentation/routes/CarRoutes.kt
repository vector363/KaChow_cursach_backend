package com.example.presentation.routes

import com.example.data.repository.CarRepositoryImpl
import com.example.data.repository.FavoriteRepositoryImpl
import com.example.presentation.models.AddCarRequest
import com.example.presentation.models.CarWithFavorite
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.carRoutes() {
    val carRepository = CarRepositoryImpl()
    val favoriteRepository = FavoriteRepositoryImpl()

    route("/car") {
        get("/all"){
            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "User not found"))
                return@get
            }
            val allCars = carRepository.getAllCar()
            val favorites = favoriteRepository.getUserFavorites(userId).map { it.id }.toSet()

            val response = allCars.map { car ->
                CarWithFavorite(
                    id = car.id,
                    brand = car.brand,
                    model = car.model,
                    dealershipId = car.dealershipId,
                    isFavorite = car.id in favorites
                )
            }
            call.respond(response)
        }

        post("/add"){
            val request = call.receive<AddCarRequest>()

            val car = carRepository.createCar(
                brand = request.brand,
                model = request.model,
                dealershipId = request.dealershipId
            )

            call.respond(HttpStatusCode.Created, car)
        }

    }
}