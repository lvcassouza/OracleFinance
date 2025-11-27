package com.oraclefinance.models.dto

import com.oraclefinance.models.TransactionType
import kotlinx.serialization.Serializable

@Serializable
data class TransactionCreateRequest(
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long
)

@Serializable
data class DashboardResponse(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val last5: List<com.oraclefinance.models.Transaction>
)
