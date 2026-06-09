package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.usecase

import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.model.HomeGreeting
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.repository.HomeRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetHomeGreetingUseCaseTest {

    @Test
    fun invoke_returnsRepositoryValue() {
        val repository = FakeHomeRepository(
            result = HomeGreeting(message = "Expected")
        )
        val useCase = GetHomeGreetingUseCase(repository)

        val result = useCase()

        assertEquals("Expected", result.message)
        assertTrue(repository.called)
    }
}

private class FakeHomeRepository(
    private val result: HomeGreeting
) : HomeRepository {
    var called: Boolean = false
        private set

    override fun greeting(): HomeGreeting {
        called = true
        return result
    }
}
