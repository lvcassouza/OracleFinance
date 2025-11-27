package com.oraclefinance.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val skip = (System.getenv("SKIP_DB_INIT") ?: System.getProperty("SKIP_DB_INIT")) == "true"
        if (skip) return
        val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/oraclefinance"
        val user = System.getenv("DB_USER") ?: "oracle_user"
        val pass = System.getenv("DB_PASSWORD") ?: "oracle_pass"
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = url
            username = user
            password = pass
            maximumPoolSize = 10
            driverClassName = "org.postgresql.Driver"
        }
        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
        transaction {
            SchemaUtils.create(UsersTable, TransactionsTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
