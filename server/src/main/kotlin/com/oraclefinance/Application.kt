package com.oraclefinance

import com.oraclefinance.di.modules
import com.oraclefinance.routes.authRoutes
import com.oraclefinance.routes.dashboardRoutes
import com.oraclefinance.routes.transactionsRoutes
import com.oraclefinance.plugins.configureMonitoring
import com.oraclefinance.plugins.configureSerialization
import com.oraclefinance.plugins.configureCors
import com.oraclefinance.plugins.configureSecurity
import com.oraclefinance.db.DatabaseFactory
import com.oraclefinance.security.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module(testModule: org.koin.core.module.Module? = null) {
    install(Koin) {
        slf4jLogger()
        modules(testModule ?: modules)
    }
    configureMonitoring()
    configureCors()
    configureSerialization()
    DatabaseFactory.init()
    val tokenConfig = TokenConfig(
        secret = System.getenv("JWT_SECRET") ?: System.getProperty("JWT_SECRET") ?: "change-me",
        issuer = System.getenv("JWT_ISSUER") ?: System.getProperty("JWT_ISSUER") ?: "oraclefinance",
        audience = System.getenv("JWT_AUDIENCE") ?: System.getProperty("JWT_AUDIENCE") ?: "oraclefinance-client",
        realm = System.getenv("JWT_REALM") ?: System.getProperty("JWT_REALM") ?: "oraclefinance"
    )
    configureSecurity(tokenConfig)
    authRoutes()
    transactionsRoutes()
    dashboardRoutes()
}
