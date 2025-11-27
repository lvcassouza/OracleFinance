package com.oraclefinance.app.data.repository

import com.oraclefinance.app.data.TokenManager
import com.oraclefinance.app.data.api.ApiService
import com.oraclefinance.app.data.dto.LoginResponse

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): LoginResponse {
        val result = api.login(email, password)
        tokenManager.saveTokens(result.access_token, result.refresh_token)
        return result
    }

    fun isLoggedIn(): Boolean = tokenManager.getAccessToken() != null

    fun logout() {
        tokenManager.clear()
    }
}

