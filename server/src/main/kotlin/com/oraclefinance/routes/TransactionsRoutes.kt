package com.oraclefinance.routes

import com.oraclefinance.models.Transaction
import com.oraclefinance.models.dto.TransactionCreateRequest
import com.oraclefinance.repositories.TransactionsRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.transactionsRoutes() {
    val repo by inject<TransactionsRepository>()
    routing {
        authenticate("auth-jwt") {
            route("/transactions") {
                get {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.subject!!.toInt()
                    val list: List<Transaction> = repo.listByUser(userId)
                    call.respond(list)
                }
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.subject!!.toInt()
                    val req = call.receive<TransactionCreateRequest>()
                    val created = repo.create(userId, req)
                    call.respond(created)
                }
            }
        }
    }
}
