package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class UiTextTest {

    @Test
    fun toStringSuspend_returnsDynamicValue() {
        val text = UiText.DynamicString("hello")
        val result = runBlocking { text.toStringSuspend() }
        assertEquals("hello", result)
    }
}
