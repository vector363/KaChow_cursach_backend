package com.example.presentation.routes

import com.example.data.repository.CarRepositoryImpl
import com.example.data.repository.FavoriteRepositoryImpl
import com.example.presentation.models.AddCarRequest
import com.example.presentation.models.CarWithFavorite
import com.example.presentation.models.UpdateCarRequest
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

        get("/dealership/{dealershipId}") {
            val dealershipId = call.parameters["dealershipId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid dealership id")

            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()
                ?: throw IllegalArgumentException("User not found")

            val cars = carRepository.getCarsByDealership(dealershipId, userId)
            call.respond(cars)
        }

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

        post("/add") {
            val request = call.receive<AddCarRequest>()

            val car = carRepository.createCar(
                brand = request.brand,
                model = request.model,
                price = request.price,
                year = request.year,
                mileage = request.mileage,
                engine = request.engine,
                horsepower = request.horsepower,
                transmission = request.transmission,
                driveUnit = request.driveUnit,
                color = request.color,
                description = request.description,
                imageUrl = request.imageUrl,
                dealershipId = request.dealershipId
            )

            call.respond(HttpStatusCode.Created, car)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid car id")

            val car = carRepository.getCarById(id)
            if (car == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Car not found"))
                return@get
            }

            call.respond(car)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid car id")

            val request = call.receive<UpdateCarRequest>()

            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                ?: throw IllegalArgumentException("User not found")

            val isFavorite = favoriteRepository.isFavorite(userId, id)

            val updatedCar = carRepository.updateCar(
                id = id,
                brand = request.brand,
                model = request.model,
                price = request.price,
                year = request.year,
                mileage = request.mileage,
                engine = request.engine,
                horsepower = request.horsepower,
                transmission = request.transmission,
                driveUnit = request.driveUnit,
                color = request.color,
                description = request.description,
                imageUrl = request.imageUrl,
                dealershipId = request.dealershipId,
            )
            call.respond(HttpStatusCode.OK, updatedCar)
        }
    }
}