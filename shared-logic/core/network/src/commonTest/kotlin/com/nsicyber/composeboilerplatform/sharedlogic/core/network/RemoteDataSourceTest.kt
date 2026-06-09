package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoteDataSourceTest {

    @Test
    fun getAsResource_mapsResponseBody() = kotlinx.coroutines.runBlocking {
        val engine = MockEngine {
            respond(
                content = "pong",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/plain")
            )
        }
        val dataSource = RemoteDataSource(HttpClient(engine))

        val result = dataSource.getAsResource<String, String>("https://example.com") {
            "remote:$it"
        }

        assertTrue(result is NetworkResource.Success)
        assertEquals("remote:pong", result.value)
    }
}
