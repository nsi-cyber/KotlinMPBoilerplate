package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.BottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.DialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.SnackbarManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BaseViewModelTest {

    @Test
    fun overlayHelpers_publishExpectedRequests() {
        val snackbarManager = FakeSnackbarManager()
        val dialogManager = FakeDialogManager()
        val bottomSheetManager = FakeBottomSheetManager()
        val viewModel = FakeBaseViewModel(
            snackbarManager = snackbarManager,
            dialogManager = dialogManager,
            bottomSheetManager = bottomSheetManager
        )

        viewModel.triggerAll()

        val firstSnackbar = snackbarManager.messages.value.first()
        assertEquals(UiText.DynamicString("error"), firstSnackbar.message)
        assertEquals(2L, firstSnackbar.id)

        val dialog = dialogManager.currentDialog.value
        assertIs<DialogRequest.Alert>(dialog)
        assertEquals(UiText.DynamicString("title"), dialog.title)

        val sheet = bottomSheetManager.currentSheet.value
        assertIs<BottomSheetRequest.Basic>(sheet)
        assertEquals(UiText.DynamicString("sheet"), sheet.title)
    }
}

private class FakeBaseViewModel(
    snackbarManager: SnackbarManager,
    dialogManager: DialogManager,
    bottomSheetManager: BottomSheetManager
) : BaseViewModel<Int, Unit, Unit, Unit>(
    initialState = 0,
    snackbarManager = snackbarManager,
    dialogManager = dialogManager,
    bottomSheetManager = bottomSheetManager
) {
    override fun process(event: Unit) = Unit

    fun triggerAll() {
        showSuccessSnack(UiText.DynamicString("success"))
        showErrorSnack(UiText.DynamicString("error"))
        showAlert(
            title = UiText.DynamicString("title"),
            message = UiText.DynamicString("message")
        )
        showBottomSheet(
            title = UiText.DynamicString("sheet"),
            message = UiText.DynamicString("content")
        )
    }
}

private class FakeSnackbarManager : SnackbarManager {
    private val state = MutableStateFlow<List<SnackbarMessage>>(emptyList())
    override val messages: StateFlow<List<SnackbarMessage>> = state

    override fun show(message: SnackbarMessage) {
        state.value = listOf(message) + state.value
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
