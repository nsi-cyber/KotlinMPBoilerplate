package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Shared Ktor HttpClient factory used by remote data sources.
 */
object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    }
                )
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.INFO
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}

/**
 * Returns the platform engine for Ktor HttpClient.
 */
expect fun platformHttpClientEngine(): HttpClientEngine

/**
 * Creates a configured platform-specific HttpClient.
 */
fun createPlatformHttpClient(): HttpClient = HttpClientFactory.create(platformHttpClientEngine())

/**
 * Shared client holder to avoid creating multiple HttpClient instances.
 */
object HttpClientProvider {
    val client: HttpClient by lazy { createPlatformHttpClient() }
}
