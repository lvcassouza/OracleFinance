package com.oraclefinance.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewTransactionRequest(
    val amount: Double,
    val description: String,
    val type: String,
    val category: String
)

@Serializable
data class TransactionResponse(val id: String)

