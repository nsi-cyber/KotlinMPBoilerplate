package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HttpClientExtTest {

    @Test
    fun toResource_mapsSuccessValue() {
        val wrapper: ResultWrapper<String> = ResultWrapper.Success("hello")

        val resource = wrapper.toResource { it.uppercase() }

        assertTrue(resource is NetworkResource.Success)
        assertEquals("HELLO", resource.value)
    }

    @Test
    fun toResource_returnsError_whenMapperReturnsNull() {
        val wrapper: ResultWrapper<String> = ResultWrapper.Success("hello")

        val resource = wrapper.toResource<String, String?> { null }

        assertTrue(resource is NetworkResource.Error)
        assertEquals("Mapper returned null.", resource.message)
    }

    @Test
    fun genericError_toResource_preservesFields() {
        val wrapper = ResultWrapper.GenericError(code = 404, message = "Not found")

        val resource = wrapper.toResourceError<Unit>()

        assertEquals(404, resource.code)
        assertEquals("Not found", resource.message)
    }
}
