package com.example.presentation.routes


import com.example.data.repository.DealershipRepositoryImpl
import com.example.domain.model.Dealership
import com.example.presentation.models.AddCarRequest
import com.example.presentation.models.AddDealershipRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.dealershipRoutes() {
    val dealershipRepository = DealershipRepositoryImpl()

    route("/dealership"){
        get("/all"){
            val dealerships = dealershipRepository.getAllDealership()
            call.respond(HttpStatusCode.OK, dealerships)
        }

        post("/add"){
            val request = call.receive<AddDealershipRequest>()

            val dealership = dealershipRepository.addDealership(
                name = request.name
            )

            call.respond(HttpStatusCode.Created, dealership)
        }
    }
}