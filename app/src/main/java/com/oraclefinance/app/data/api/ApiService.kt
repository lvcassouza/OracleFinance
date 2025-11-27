package com.oraclefinance.app.data.api

import com.oraclefinance.app.data.dto.AnalyzeResponse
import com.oraclefinance.app.data.dto.DashboardSummary
import com.oraclefinance.app.data.dto.LoginRequest
import com.oraclefinance.app.data.dto.LoginResponse
import com.oraclefinance.app.data.dto.NewTransactionRequest
import com.oraclefinance.app.data.dto.TransactionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiService(private val client: HttpClient, private val baseUrl: String) {
    suspend fun login(email: String, password: String): LoginResponse {
        val response = client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        return response.body()
    }

    suspend fun dashboardSummary(): DashboardSummary {
        val response = client.get("$baseUrl/dashboard/summary")
        return response.body()
    }

    suspend fun addTransaction(request: NewTransactionRequest): TransactionResponse {
        val response = client.post("$baseUrl/transactions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    suspend fun analyze(): AnalyzeResponse {
        val response = client.post("$baseUrl/ai/analyze")
        return response.body()
    }
}

