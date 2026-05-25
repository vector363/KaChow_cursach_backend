package com.example.presentation.routes

import com.example.data.repository.DealershipRepositoryImpl
import com.example.presentation.models.AddDealershipRequest
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


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
                name = request.name,
                address = request.address,
                rating = request.rating
            )

            call.respond(HttpStatusCode.Created, dealership)
        }

        post("/{id}/image") {
            val dealershipId = call.parameters["id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid dealership id")

            val multipart = call.receiveMultipart()
            var imageUrl: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileName = part.originalFileName ?: "image.jpg"
                    val extension = fileName.substringAfterLast(".", "jpg")
                    val savedName = "dealership_$dealershipId.$extension"
                    val folder = File("uploads/dealerships/")
                    folder.mkdirs()

                    val file = File(folder, savedName)
                    part.streamProvider().use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    imageUrl = "/uploads/dealerships/$savedName"
                }
                part.dispose()
            }

            if (imageUrl != null) {
                val updated = dealershipRepository.updateImage(dealershipId, imageUrl)
                call.respond(updated ?: mapOf("error" to "Dealership not found"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No image uploaded"))
            }
        }


        delete("/{id}/image") {
            val dealershipId = call.parameters["id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid dealership id")

            val dealerships = dealershipRepository.getAllDealership()
            val dealership = dealerships.find { it.id == dealershipId }

            if (dealership?.imageUrl != null) {
                val file = File(dealership.imageUrl.removePrefix("/"))
                if (file.exists()) {
                    file.delete()
                }
            }

            val updated = dealershipRepository.updateImage(dealershipId, null)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Image deleted", "dealership" to updated))
        }
    }


}