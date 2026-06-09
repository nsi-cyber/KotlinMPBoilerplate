package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.ensureActive
import kotlinx.io.IOException
import kotlin.coroutines.coroutineContext

/**
 * Public result model used by repositories to expose network operation outcomes.
 */
sealed interface NetworkResource<T> {
    data class Success<T>(
        val value: T,
        val message: String? = null
    ) : NetworkResource<T>

    data class Error<T>(
        val code: Int? = null,
        val message: String? = null,
        val throwable: Throwable? = null,
        val internalError: Boolean = false
    ) : NetworkResource<T>
}

/**
 * Internal low-level wrapper for transport and parsing outcomes.
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T, val message: String? = null) : ResultWrapper<T>()
    data class GenericError(
        val code: Int? = null,
        val message: String? = null,
        val throwable: Throwable? = null,
        val internalError: Boolean = false
    ) : ResultWrapper<Nothing>()

    data class NetworkError(val throwable: IOException? = null) : ResultWrapper<Nothing>()
}

/**
 * Maps low-level wrapper values into repository-level NetworkResource.
 */
fun <T, R> ResultWrapper<T>.toResource(mapper: (T) -> R?): NetworkResource<R> {
    return when (this) {
        is ResultWrapper.Success -> {
            val mapped = mapper(value)
            if (mapped != null) {
                NetworkResource.Success(mapped, message)
            } else {
                NetworkResource.Error(message = "Mapper returned null.")
            }
        }

        is ResultWrapper.GenericError -> toResourceError()
        is ResultWrapper.NetworkError -> toResourceError()
    }
}

/**
 * Converts any ResultWrapper failure into NetworkResource.Error.
 */
fun <T> ResultWrapper.GenericError.toResourceError(): NetworkResource.Error<T> {
    return NetworkResource.Error(code, message, throwable, internalError)
}

/**
 * Converts a network failure into NetworkResource.Error.
 */
fun <T> ResultWrapper.NetworkError.toResourceError(): NetworkResource.Error<T> {
    return NetworkResource.Error(throwable = throwable)
}

/**
 * Executes a request and wraps all transport and cancellation edge-cases.
 */
suspend inline fun <reified T> safeApiCall(
    crossinline execute: suspend () -> HttpResponse
): ResultWrapper<T> {
    val response = try {
        execute()
    } catch (e: IOException) {
        return ResultWrapper.NetworkError(e)
    } catch (e: Throwable) {
        coroutineContext.ensureActive()
        return ResultWrapper.GenericError(throwable = e, message = e.message)
    }

    return responseToResult(response)
}

/**
 * Parses HTTP responses into typed ResultWrapper values.
 */
suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): ResultWrapper<T> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                ResultWrapper.Success(value = response.body<T>())
            } catch (e: NoTransformationFoundException) {
                ResultWrapper.GenericError(
                    message = "Serialization error: ${e.message}",
                    throwable = e
                )
            } catch (e: Throwable) {
                ResultWrapper.GenericError(
                    message = "Unexpected deserialization error.",
                    throwable = e
                )
            }
        }

        else -> {
            ResultWrapper.GenericError(
                code = response.status.value,
                message = response.status.description
            )
        }
    }
}
