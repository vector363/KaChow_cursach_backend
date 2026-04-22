package org.example.com.example

import com.example.data.database.DatabaseFactory
import com.example.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()
        configureCallLogging()
        configureStatusPages()
        configureRouting()
    }.start(wait = true)
}