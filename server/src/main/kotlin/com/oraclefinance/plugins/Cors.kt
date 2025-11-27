package com.oraclefinance.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        val origins = System.getenv("CORS_ORIGINS")
        if (origins.isNullOrBlank()) {
            anyHost()
        } else {
            origins.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { allowHost(it) }
        }
    }
}
