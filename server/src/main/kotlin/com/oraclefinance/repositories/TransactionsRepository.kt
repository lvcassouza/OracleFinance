package com.oraclefinance.repositories

import com.oraclefinance.db.DatabaseFactory
import com.oraclefinance.db.TransactionsTable
import com.oraclefinance.models.Transaction
import com.oraclefinance.models.TransactionType
import org.jetbrains.exposed.sql.*

open class TransactionsRepository {
    suspend fun listByUser(userId: Int): List<Transaction> = DatabaseFactory.dbQuery {
        val uid = org.jetbrains.exposed.dao.id.EntityID(userId, com.oraclefinance.db.UsersTable)
        TransactionsTable.select { TransactionsTable.userId eq uid }
            .orderBy(TransactionsTable.date, SortOrder.DESC)
            .map { toTransaction(it) }
    }

    suspend fun create(userId: Int, req: com.oraclefinance.models.dto.TransactionCreateRequest): Transaction = DatabaseFactory.dbQuery {
        val id = TransactionsTable.insertAndGetId {
            it[TransactionsTable.userId] = org.jetbrains.exposed.dao.id.EntityID(userId, com.oraclefinance.db.UsersTable)
            it[TransactionsTable.description] = req.description
            it[TransactionsTable.amount] = req.amount
            it[TransactionsTable.type] = req.type.name
            it[TransactionsTable.category] = req.category
            it[TransactionsTable.date] = req.date
        }.value
        Transaction(id, userId, req.description, req.amount, req.type, req.category, req.date)
    }

    suspend fun totals(userId: Int): Pair<Double, Double> = DatabaseFactory.dbQuery {
        val uid = org.jetbrains.exposed.dao.id.EntityID(userId, com.oraclefinance.db.UsersTable)
        val income = TransactionsTable.slice(TransactionsTable.amount.sum()).select { (TransactionsTable.userId eq uid) and (TransactionsTable.type eq TransactionType.INCOME.name) }.singleOrNull()?.getOrNull(TransactionsTable.amount.sum()) ?: 0.0
        val expense = TransactionsTable.slice(TransactionsTable.amount.sum()).select { (TransactionsTable.userId eq uid) and (TransactionsTable.type eq TransactionType.EXPENSE.name) }.singleOrNull()?.getOrNull(TransactionsTable.amount.sum()) ?: 0.0
        income to expense
    }

    suspend fun last5(userId: Int): List<Transaction> = DatabaseFactory.dbQuery {
        val uid = org.jetbrains.exposed.dao.id.EntityID(userId, com.oraclefinance.db.UsersTable)
        TransactionsTable.select { TransactionsTable.userId eq uid }
            .orderBy(TransactionsTable.date, SortOrder.DESC)
            .limit(5)
            .map { toTransaction(it) }
    }

    private fun toTransaction(row: ResultRow) = Transaction(
        id = row[TransactionsTable.id].value,
        userId = row[TransactionsTable.userId].value,
        description = row[TransactionsTable.description],
        amount = row[TransactionsTable.amount],
        type = TransactionType.valueOf(row[TransactionsTable.type]),
        category = row[TransactionsTable.category],
        date = row[TransactionsTable.date]
    )
}
