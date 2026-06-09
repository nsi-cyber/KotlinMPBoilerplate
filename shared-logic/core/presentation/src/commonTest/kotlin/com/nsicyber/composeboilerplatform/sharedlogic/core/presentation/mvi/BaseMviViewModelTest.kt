package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.mvi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaseMviViewModelTest {

    @Test
    fun process_updatesStateThroughBaseHelper() {
        val viewModel = TestMviViewModel()

        viewModel.process(TestEvent.Increment)
        viewModel.process(TestEvent.Increment)

        assertEquals(2, viewModel.state.value.count)
    }

    @Test
    fun setNavigator_bindsNavigatorReference() {
        val viewModel = TestMviViewModel()
        val navigator = FakeTestNavigator()

        viewModel.setNavigator(navigator)
        viewModel.process(TestEvent.Navigate)

        assertTrue(navigator.navigateCalled)
    }
}

private data class TestState(
    val count: Int = 0
)

private sealed interface TestEvent {
    data object Increment : TestEvent
    data object Navigate : TestEvent
}

private interface TestNavigator {
    fun navigate()
}

private class TestMviViewModel :
    BaseMviViewModel<TestState, Nothing, TestEvent, TestNavigator>(TestState()) {
    override fun process(event: TestEvent) {
        when (event) {
            TestEvent.Increment -> updateState { it.copy(count = it.count + 1) }
            TestEvent.Navigate -> navigator?.navigate()
        }
    }
}

private class FakeTestNavigator : TestNavigator {
    var navigateCalled: Boolean = false
        private set

    override fun navigate() {
        navigateCalled = true
    }
}
