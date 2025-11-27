package com.oraclefinance.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val access_token: String, val refresh_token: String)

