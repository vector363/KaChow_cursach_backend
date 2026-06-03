package com.example.presentation.routes

import com.example.data.repository.CarImageRepositoryImpl
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


fun Route.imageRoutes() {
    val imageRepository = CarImageRepositoryImpl()

    route("/car/{carId}/images") {
        get {
            val carId = call.parameters["carId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid car id")
            val images = imageRepository.getImagesByCar(carId)
            call.respond(images)
        }

        post {
            val carId = call.parameters["carId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid car id")
            val multipart = call.receiveMultipart()
            var imageUrl: String? = null
            var isPreview = false

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileName = part.originalFileName ?: "image.jpg"
                    val extension = fileName.substringAfterLast(".", "jpg")
                    val savedName = "${System.currentTimeMillis()}.$extension"
                    val folder = File("uploads/cars/$carId/")
                    folder.mkdirs()

                    val file = File(folder, savedName)
                    part.streamProvider().use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    imageUrl = "/uploads/cars/$carId/$savedName"

                    val previewHeader = part.headers.getAll("X-Is-Preview")?.firstOrNull()
                    isPreview = previewHeader == "true" || previewHeader == "1"
                }
                part.dispose()
            }

            if (imageUrl != null) {
                val image = imageRepository.addImage(carId, imageUrl!!, isPreview)
                call.respond(HttpStatusCode.Created, image)
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No image uploaded"))
            }
        }

        delete("/{imageId}") {
            val imageId = call.parameters["imageId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid image id")

            val carId = call.parameters["carId"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid car id")

            val images = imageRepository.getImagesByCar(carId)
            val imageToDelete = images.find { it.id == imageId }

            if (imageToDelete != null) {
                val file = File(imageToDelete.imageUrl.removePrefix("/"))
                if (file.exists()) {
                    file.delete()
                }
            }

            imageRepository.deleteImage(imageId)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Image deleted"))
        }
    }

    put("/car/{carId}/preview/{imageId}") {
        val carId = call.parameters["carId"]?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid car id")
        val imageId = call.parameters["imageId"]?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid image id")

        imageRepository.setPreview(carId, imageId)
        call.respond(HttpStatusCode.OK, mapOf("message" to "Preview updated"))
    }
}