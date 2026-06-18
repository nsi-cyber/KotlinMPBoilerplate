package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Generic remote datasource helper inspired by feature-specific datasource layers.
 *
 * Features can compose this class or subclass BaseRemoteDataSource for endpoint-specific logic.
 */
open class RemoteDataSource(
    @PublishedApi
    internal val httpClient: HttpClient = HttpClientProvider.client
) : BaseRemoteDataSource() {

    /**
     * Executes a GET request and maps response body to domain-facing resource.
     */
    suspend inline fun <reified T, R> getAsResource(
        url: String,
        crossinline mapper: (T) -> R?
    ): NetworkResource<R> {
        return callAsResource<T, R>(
            execute = { httpClient.get(urlString = url) },
            mapper = { mapper(it) }
        )
    }

    /**
     * Executes a POST request with body and maps response body to domain-facing resource.
     */
    suspend inline fun <reified T, reified B : Any, R> postAsResource(
        url: String,
        body: B,
        crossinline mapper: (T) -> R?
    ): NetworkResource<R> {
        return callAsResource<T, R>(
            execute = {
                httpClient.post(urlString = url) {
                    setBody(body)
                }
            },
            mapper = { mapper(it) }
        )
    }
}
