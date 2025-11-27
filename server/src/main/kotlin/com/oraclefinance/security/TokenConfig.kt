package com.oraclefinance.security

data class TokenConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)
