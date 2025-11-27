package com.oraclefinance.routes

import com.oraclefinance.models.dto.DashboardResponse
import com.oraclefinance.repositories.TransactionsRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.dashboardRoutes() {
    val repo by inject<TransactionsRepository>()
    routing {
        authenticate("auth-jwt") {
            get("/dashboard") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.subject!!.toInt()
                val totals = repo.totals(userId)
                val last5 = repo.last5(userId)
                val balance = totals.first - totals.second
                val res = DashboardResponse(totals.first, totals.second, balance, last5)
                call.respond(res)
            }
        }
    }
}
