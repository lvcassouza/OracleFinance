package com.oraclefinance.db

import org.jetbrains.exposed.dao.id.IntIdTable

object TransactionsTable : IntIdTable("transactions") {
    val userId = reference("user_id", UsersTable)
    val description = varchar("description", 255)
    val amount = double("amount")
    val type = varchar("type", 20)
    val category = varchar("category", 100)
    val date = long("date")
}
