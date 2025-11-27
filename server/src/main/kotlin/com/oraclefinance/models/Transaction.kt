package com.oraclefinance.models

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType { INCOME, EXPENSE }

@Serializable
data class Transaction(
    val id: Int,
    val userId: Int,
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long
)
