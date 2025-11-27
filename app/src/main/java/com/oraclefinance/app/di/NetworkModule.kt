package com.oraclefinance.app.di

import android.content.Context
import com.oraclefinance.app.data.TokenManager
import com.oraclefinance.app.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single { TokenManager(androidContext()) }

    single {
        val context: Context = androidContext()
        val tokenManager: TokenManager = get()
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    }
                )
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenManager.getAccessToken()
                        val refresh = tokenManager.getRefreshToken()
                        if (access != null && refresh != null) BearerTokens(access, refresh) else null
                    }
                    refreshTokens {
                        val refresh = tokenManager.getRefreshToken()
                        if (refresh == null) null else try {
                            val response = client.post("${BuildConfig.BASE_URL}/auth/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(RefreshRequest(refresh))
                            }
                            val tokens = response.body<RefreshResponse>()
                            tokenManager.saveTokens(tokens.access_token, tokens.refresh_token)
                            BearerTokens(tokens.access_token, tokens.refresh_token)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        val path = request.url.fullPath
                        !(path.contains("/auth/login") || path.contains("/auth/refresh"))
                    }
                }
            }
        }
    }
}

@Serializable
data class RefreshRequest(val refresh_token: String)

@Serializable
data class RefreshResponse(val access_token: String, val refresh_token: String)
