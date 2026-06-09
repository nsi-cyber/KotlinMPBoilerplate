package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.BottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.DialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.SnackbarManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.UiText
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.model.HomeGreeting
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.repository.HomeRepository
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.usecase.GetHomeGreetingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HomeViewModelTest {

    @Test
    fun onLoadGreetingClick_updatesUiState() {
        val viewModel = createViewModel(greetingMessage = "Hello test")

        viewModel.onLoadGreetingClick()

        assertEquals("Hello test", viewModel.uiState.value.greetingText)
    }

    @Test
    fun onShowSnackbarClick_sendsSuccessSnackbar() {
        val snackbarManager = FakeSnackbarManager()
        val viewModel = createViewModel(snackbarManager = snackbarManager)

        viewModel.onShowSnackbarClick()

        val sent = snackbarManager.messages.value.single()
        assertEquals(UiText.DynamicString("Home snackbar demo from shared feature."), sent.message)
        assertEquals(1L, sent.id)
    }

    @Test
    fun onShowDialogClick_sendsAlertDialogRequest() {
        val dialogManager = FakeDialogManager()
        val viewModel = createViewModel(dialogManager = dialogManager)

        viewModel.onShowDialogClick()

        val request = dialogManager.currentDialog.value
        assertIs<DialogRequest.Alert>(request)
        assertEquals(UiText.DynamicString("Demo Dialog"), request.title)
    }

    @Test
    fun onShowBottomSheetClick_sendsBasicBottomSheetRequest() {
        val bottomSheetManager = FakeBottomSheetManager()
        val viewModel = createViewModel(bottomSheetManager = bottomSheetManager)

        viewModel.onShowBottomSheetClick()

        val request = bottomSheetManager.currentSheet.value
        assertIs<BottomSheetRequest.Basic>(request)
        assertEquals(UiText.DynamicString("Demo Bottom Sheet"), request.title)
    }

    @Test
    fun onNavigateDemoClick_callsBoundNavigator() {
        val homeNavigator = FakeHomeNavigator()
        val viewModel = createViewModel()
        viewModel.setNavigator(homeNavigator)

        viewModel.onNavigateDemoClick()

        assertTrue(homeNavigator.navigateCalled)
    }

    private fun createViewModel(
        greetingMessage: String = "Hello",
        snackbarManager: FakeSnackbarManager = FakeSnackbarManager(),
        dialogManager: FakeDialogManager = FakeDialogManager(),
        bottomSheetManager: FakeBottomSheetManager = FakeBottomSheetManager()
    ): HomeViewModel {
        val useCase = GetHomeGreetingUseCase(
            object : HomeRepository {
                override fun greeting(): HomeGreeting = HomeGreeting(greetingMessage)
            }
        )
        return HomeViewModel(
            getHomeGreetingUseCase = useCase,
            snackbarManager = snackbarManager,
            dialogManager = dialogManager,
            bottomSheetManager = bottomSheetManager
        )
    }
}

private class FakeSnackbarManager : SnackbarManager {
    private val state = MutableStateFlow<List<SnackbarMessage>>(emptyList())
    override val messages: StateFlow<List<SnackbarMessage>> = state

    override fun show(message: SnackbarMessage) {
        state.value = state.value + message
    }

    override fun dismiss(messageId: Long) {
        state.value = state.value.filterNot { it.id == messageId }
    }
}

private class FakeDialogManager : DialogManager {
    private val state = MutableStateFlow<DialogRequest?>(null)
    override val currentDialog: StateFlow<DialogRequest?> = state

    override fun show(dialogRequest: DialogRequest) {
        state.value = dialogRequest
    }

    override fun dismiss() {
        state.value = null
    }
}

private class FakeBottomSheetManager : BottomSheetManager {
    private val state = MutableStateFlow<BottomSheetRequest?>(null)
    override val currentSheet: StateFlow<BottomSheetRequest?> = state

    override fun show(sheetRequest: BottomSheetRequest) {
        state.value = sheetRequest
    }

    override fun dismiss() {
        state.value = null
    }
}

private class FakeHomeNavigator : HomeNavigator {
    var navigateCalled: Boolean = false
        private set

    override fun navigateBack() = Unit

    override fun navigateToHomeDetailsDemo() {
        navigateCalled = true
    }
}
