package com.oraclefinance.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardSummary(
    val income: Double,
    val expense: Double,
    val balance: Double,
    val last7Days: List<DaySpending>
)

@Serializable
data class DaySpending(
    val dayLabel: String,
    val amount: Double
)

