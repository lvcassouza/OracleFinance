package com.oraclefinance.di

import com.oraclefinance.repositories.TransactionsRepository
import com.oraclefinance.repositories.UsersRepository
import org.koin.dsl.module

class UsersRepositoryFake : UsersRepository() {
    private val users = mutableMapOf<String, Int>()
    private var nextId = 1
    override suspend fun findByEmail(email: String) = users[email]?.let { com.oraclefinance.models.User(it, email) }
    override suspend fun create(email: String, password: String): com.oraclefinance.models.User {
        val id = nextId++
        users[email] = id
        return com.oraclefinance.models.User(id, email)
    }
    override suspend fun verify(email: String, password: String) = findByEmail(email)
}

class TransactionsRepositoryFake : TransactionsRepository() {
    private val list = mutableListOf<com.oraclefinance.models.Transaction>()
    private var nextId = 1
    override suspend fun listByUser(userId: Int) = list.filter { it.userId == userId }.sortedByDescending { it.date }
    override suspend fun create(userId: Int, req: com.oraclefinance.models.dto.TransactionCreateRequest): com.oraclefinance.models.Transaction {
        val t = com.oraclefinance.models.Transaction(nextId++, userId, req.description, req.amount, req.type, req.category, req.date)
        list.add(t)
        return t
    }
    override suspend fun totals(userId: Int): Pair<Double, Double> {
        val income = list.filter { it.userId == userId && it.type == com.oraclefinance.models.TransactionType.INCOME }.sumOf { it.amount }
        val expense = list.filter { it.userId == userId && it.type == com.oraclefinance.models.TransactionType.EXPENSE }.sumOf { it.amount }
        return income to expense
    }
    override suspend fun last5(userId: Int) = list.filter { it.userId == userId }.sortedByDescending { it.date }.take(5)
}

val testModules = module {
    single<UsersRepository> { UsersRepositoryFake() }
    single<TransactionsRepository> { TransactionsRepositoryFake() }
}
