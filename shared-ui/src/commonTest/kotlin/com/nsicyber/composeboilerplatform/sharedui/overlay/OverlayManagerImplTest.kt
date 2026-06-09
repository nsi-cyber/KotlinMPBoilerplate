package com.nsicyber.composeboilerplatform.sharedui.overlay

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.UiText
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessageType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OverlayManagerImplTest {

    @Test
    fun snackbarManager_showAndDismiss_updatesQueue() {
        val manager = SnackbarManagerImpl()

        manager.show(SnackbarMessage(id = 1L, message = UiText.DynamicString("first"), type = SnackbarMessageType.INFO))
        manager.show(SnackbarMessage(id = 2L, message = UiText.DynamicString("second"), type = SnackbarMessageType.SUCCESS))

        assertEquals(listOf(2L, 1L), manager.messages.value.map { it.id })

        manager.dismiss(2L)

        assertEquals(listOf(1L), manager.messages.value.map { it.id })
    }

    @Test
    fun snackbarManager_convenienceMethods_generateExpectedTypesAndIds() {
        val manager = SnackbarManagerImpl()

        manager.showSuccess("ok")
        manager.showError("fail")

        val messages = manager.messages.value
        assertEquals(2, messages.size)
        assertEquals(2L, messages[0].id)
        assertEquals(SnackbarMessageType.ERROR, messages[0].type)
        assertEquals(1L, messages[1].id)
        assertEquals(SnackbarMessageType.SUCCESS, messages[1].type)
    }

    @Test
    fun dialogManager_showAndDismiss_setsCurrentDialog() {
        val manager = DialogManagerImpl()
        val request = DialogRequest.Alert(
            title = UiText.DynamicString("Title"),
            message = UiText.DynamicString("Message")
        )

        manager.show(request)
        assertEquals(request, manager.currentDialog.value)

        manager.dismiss()
        assertNull(manager.currentDialog.value)
    }

    @Test
    fun bottomSheetManager_showAndDismiss_setsCurrentSheet() {
        val manager = BottomSheetManagerImpl()
        val request = BottomSheetRequest.Basic(
            title = UiText.DynamicString("Sheet"),
            message = UiText.DynamicString("Content")
        )

        manager.show(request)
        assertEquals(request, manager.currentSheet.value)

        manager.dismiss()
        assertNull(manager.currentSheet.value)
    }
}
