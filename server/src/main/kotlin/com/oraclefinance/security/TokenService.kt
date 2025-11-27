package com.oraclefinance.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

object TokenService {
    private lateinit var config: TokenConfig
    private lateinit var algorithm: Algorithm

    fun configure(tokenConfig: TokenConfig) {
        config = tokenConfig
        algorithm = Algorithm.HMAC256(tokenConfig.secret)
    }

    fun generateAccessToken(id: Int, email: String): String {
        val expiresAt = java.util.Date(System.currentTimeMillis() + 15 * 60 * 1000)
        return JWT.create()
            .withSubject(id.toString())
            .withClaim("email", email)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withExpiresAt(expiresAt)
            .sign(algorithm)
    }

    fun generateRefreshToken(id: Int, email: String): String {
        val expiresAt = java.util.Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)
        return JWT.create()
            .withSubject(id.toString())
            .withClaim("email", email)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withExpiresAt(expiresAt)
            .sign(algorithm)
    }
}
