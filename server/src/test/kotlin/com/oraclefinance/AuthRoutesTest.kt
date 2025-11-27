package com.oraclefinance

import com.oraclefinance.di.testModules
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Serializable
data class LoginResponseTest(val accessToken: String, val refreshToken: String)

class AuthRoutesTest {
    @Test
    fun registerAndLogin() = testApplication {
        System.setProperty("SKIP_DB_INIT", "true")
        System.setProperty("JWT_SECRET", "testsecret")
        System.setProperty("JWT_ISSUER", "oraclefinance")
        System.setProperty("JWT_AUDIENCE", "oraclefinance-client")
        System.setProperty("JWT_REALM", "oraclefinance")

        application { module(testModule = testModules) }

        val email = "user@test.com"
        val password = "123"

        val reg = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("{" + "\"email\":\"$email\",\"password\":\"$password\"" + "}")
        }
        assertEquals(HttpStatusCode.OK, reg.status)

        val login = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{" + "\"email\":\"$email\",\"password\":\"$password\"" + "}")
        }
        assertEquals(HttpStatusCode.OK, login.status)
        val body = login.bodyAsText()
        val res = Json.decodeFromString<LoginResponseTest>(body)
        assert(res.accessToken.isNotBlank())
        assert(res.refreshToken.isNotBlank())
    }
}
