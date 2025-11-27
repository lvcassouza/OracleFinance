package com.oraclefinance

import com.oraclefinance.di.testModules
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ProtectedRoutesTest {
    @Test
    fun createTransactionAndGetDashboard() = testApplication {
        System.setProperty("SKIP_DB_INIT", "true")
        System.setProperty("JWT_SECRET", "testsecret")
        System.setProperty("JWT_ISSUER", "oraclefinance")
        System.setProperty("JWT_AUDIENCE", "oraclefinance-client")
        System.setProperty("JWT_REALM", "oraclefinance")

        application { module(testModule = testModules) }

        val email = "user2@test.com"
        val password = "456"
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("{" + "\"email\":\"$email\",\"password\":\"$password\"" + "}")
        }
        val login = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{" + "\"email\":\"$email\",\"password\":\"$password\"" + "}")
        }
        val token = kotlinx.serialization.json.Json.parseToJsonElement(login.bodyAsText()).jsonObject["accessToken"]!!.jsonPrimitive.content

        val now = System.currentTimeMillis()
        val create = client.post("/transactions") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody("{" + "\"description\":\"Salário\",\"amount\":1000.0,\"type\":\"INCOME\",\"category\":\"Work\",\"date\":$now" + "}")
        }
        assertEquals(HttpStatusCode.OK, create.status)

        val dashboard = client.get("/dashboard") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, dashboard.status)
        val text = dashboard.bodyAsText()
        assert(text.contains("totalIncome"))
        assert(text.contains("balance"))
    }
}
