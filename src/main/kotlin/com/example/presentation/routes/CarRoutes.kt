package com.example.presentation.routes

import com.example.data.repository.CarRepositoryImpl
import com.example.presentation.models.AddCarRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.carRoutes() {
    val carRepository = CarRepositoryImpl()

    route("/car") {
        get("/all"){
            val cars = carRepository.getAllCar()
            call.respond(HttpStatusCode.OK, cars)
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