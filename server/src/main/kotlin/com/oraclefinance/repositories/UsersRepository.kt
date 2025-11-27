package com.oraclefinance.repositories

import com.oraclefinance.db.DatabaseFactory
import com.oraclefinance.db.UsersTable
import com.oraclefinance.models.User
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt

open class UsersRepository {
    suspend fun findByEmail(email: String): User? = DatabaseFactory.dbQuery {
        UsersTable.select { UsersTable.email eq email }.limit(1).singleOrNull()?.let { toUser(it) }
    }

    suspend fun create(email: String, password: String): User = DatabaseFactory.dbQuery {
        val hash = BCrypt.hashpw(password, BCrypt.gensalt())
        val id = UsersTable.insertAndGetId {
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = hash
        }.value
        User(id, email)
    }

    suspend fun verify(email: String, password: String): User? = DatabaseFactory.dbQuery {
        UsersTable.select { UsersTable.email eq email }.limit(1).singleOrNull()?.let {
            val hash = it[UsersTable.passwordHash]
            if (BCrypt.checkpw(password, hash)) toUser(it) else null
        }
    }

    private fun toUser(row: ResultRow) = User(row[UsersTable.id].value, row[UsersTable.email])
}
