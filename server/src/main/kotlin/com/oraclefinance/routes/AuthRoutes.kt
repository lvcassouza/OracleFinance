package com.oraclefinance.routes

import com.oraclefinance.models.dto.LoginRequest
import com.oraclefinance.models.dto.LoginResponse
import com.oraclefinance.models.dto.RegisterRequest
import com.oraclefinance.repositories.UsersRepository
import com.oraclefinance.security.TokenService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.authRoutes() {
    val usersRepo by inject<UsersRepository>()
    routing {
        route("/auth") {
            post("/register") {
                val req = call.receive<RegisterRequest>()
                val existing = usersRepo.findByEmail(req.email)
                if (existing != null) {
                    call.respondText("Email já cadastrado", status = io.ktor.http.HttpStatusCode.Conflict)
                } else {
                    val user = usersRepo.create(req.email, req.password)
                    call.respond(user)
                }
            }
            post("/login") {
                val req = call.receive<LoginRequest>()
                val user = usersRepo.verify(req.email, req.password)
                if (user == null) {
                    call.respondText("Credenciais inválidas", status = io.ktor.http.HttpStatusCode.Unauthorized)
                } else {
                    val access = TokenService.generateAccessToken(user.id, user.email)
                    val refresh = TokenService.generateRefreshToken(user.id, user.email)
                    call.respond(LoginResponse(access, refresh))
                }
            }
        }
    }
}
