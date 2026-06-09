package com.nsicyber.composeboilerplatform.sharedlogic.core.network

/**
 * Base remote datasource helper for building feature-specific network datasources.
 */
abstract class BaseRemoteDataSource {

    /**
     * Executes a typed API call and maps result to repository-level resource model.
     */
    @PublishedApi
    internal suspend inline fun <reified T, R> callAsResource(
        crossinline execute: suspend () -> io.ktor.client.statement.HttpResponse,
        crossinline mapper: (T) -> R?
    ): NetworkResource<R> {
        return safeApiCall<T> { execute() }.toResource { mapper(it) }
    }
}
