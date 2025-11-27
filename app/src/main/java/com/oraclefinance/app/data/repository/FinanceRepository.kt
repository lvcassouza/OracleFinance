package com.oraclefinance.app.data.repository

import com.oraclefinance.app.data.api.ApiService
import com.oraclefinance.app.data.dto.AnalyzeResponse
import com.oraclefinance.app.data.dto.DashboardSummary
import com.oraclefinance.app.data.dto.NewTransactionRequest
import com.oraclefinance.app.data.dto.TransactionResponse

class FinanceRepository(private val api: ApiService) {
    suspend fun getDashboard(): DashboardSummary = api.dashboardSummary()

    suspend fun addTransaction(req: NewTransactionRequest): TransactionResponse = api.addTransaction(req)

    suspend fun analyze(): AnalyzeResponse = api.analyze()
}

