package com.example.presentation.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


fun Route.staticRoutes() {
    get("/uploads/{path...}") {
        val pathSegments = call.parameters.getAll("path")

        if (pathSegments == null || pathSegments.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid path"))
            return@get
        }

        val currentDirectory = File("").absoluteFile
        val uploadsDir = File(currentDirectory, "uploads")
        var currentFile = uploadsDir


        for (segment in pathSegments) {
            currentFile = File(currentFile, segment)
        }
        val finalFile = currentFile



        if (finalFile.exists() && !finalFile.isDirectory) {
            call.respondFile(finalFile)
        } else {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to "File not found or is a directory"))
        }
    }
}