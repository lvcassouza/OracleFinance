package com.oraclefinance.plugins

import com.oraclefinance.security.TokenConfig
import com.oraclefinance.security.TokenService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

fun Application.configureSecurity(tokenConfig: TokenConfig) {
    val algorithm = Algorithm.HMAC256(tokenConfig.secret)
    install(Authentication) {
        jwt("auth-jwt") {
            realm = tokenConfig.realm
            verifier(
                JWT
                    .require(algorithm)
                    .withIssuer(tokenConfig.issuer)
                    .withAudience(tokenConfig.audience)
                    .build()
            )
            validate { credential ->
                val subject = credential.payload.subject
                if (!subject.isNullOrBlank()) JWTPrincipal(credential.payload) else null
            }
        }
    }
    TokenService.configure(tokenConfig)
}
