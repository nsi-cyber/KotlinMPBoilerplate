package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.BaseViewModel
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.UiText
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.BottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.DialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.LocalBottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.LocalDialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.LocalSnackbarManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.SnackbarManager
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.data.repository.HomeRepositoryImpl
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.usecase.GetHomeGreetingUseCase
import kotlinx.coroutines.flow.StateFlow

/**
 * Minimal shared state holder for Home feature presentation logic.
 */
class HomeViewModel(
    private val getHomeGreetingUseCase: GetHomeGreetingUseCase,
    snackbarManager: SnackbarManager,
    dialogManager: DialogManager,
    bottomSheetManager: BottomSheetManager
) : BaseViewModel<HomeUiState, HomeEffect, HomeEvent, HomeNavigator>(
    initialState = HomeUiState(),
    snackbarManager = snackbarManager,
    dialogManager = dialogManager,
    bottomSheetManager = bottomSheetManager
) {
    val uiState: StateFlow<HomeUiState> = state

    override fun process(event: HomeEvent) {
        when (event) {
            HomeEvent.OnLoadGreetingClicked -> {
                val greeting = getHomeGreetingUseCase()
                updateState { it.copy(greetingText = greeting.message) }
            }

            HomeEvent.OnShowSnackbarClicked -> onShowSnackbarClick()
            HomeEvent.OnShowDialogClicked -> onShowDialogClick()
            HomeEvent.OnShowBottomSheetClicked -> onShowBottomSheetClick()
            HomeEvent.OnNavigateDemoClicked -> navigator?.navigateToHomeDetailsDemo()
        }
    }

    fun onLoadGreetingClick() {
        process(HomeEvent.OnLoadGreetingClicked)
    }

    /**
     * Shows a filler success snackbar to validate the global snackbar pipeline.
     */
    fun onShowSnackbarClick() {
        showSuccessSnack(
            message = UiText.DynamicString("Home snackbar demo from shared feature.")
        )
    }

    /**
     * Shows a filler alert dialog to validate the global dialog pipeline.
     */
    fun onShowDialogClick() {
        showAlert(
            title = UiText.DynamicString("Demo Dialog"),
            message = UiText.DynamicString("This is a filler dialog wired from HomeViewModel.")
        )
    }

    /**
     * Shows a filler bottom sheet to validate the global bottom sheet pipeline.
     */
    fun onShowBottomSheetClick() {
        showBottomSheet(
            title = UiText.DynamicString("Demo Bottom Sheet"),
            message = UiText.DynamicString("This is a filler bottom sheet from shared Home presentation.")
        )
    }

    /**
     * Triggers a navigator-based demo action.
     */
    fun onNavigateDemoClick() {
        process(HomeEvent.OnNavigateDemoClicked)
    }
}

/**
 * Creates and remembers the default HomeViewModel wiring.
 */
@Composable
fun rememberHomeViewModel(): HomeViewModel {
    return rememberHomeViewModel(homeNavigator = null)
}

/**
 * Creates and remembers HomeViewModel with optional navigator binding.
 */
@Composable
fun rememberHomeViewModel(
    homeNavigator: HomeNavigator?
): HomeViewModel {
    val snackbarManager = LocalSnackbarManager.current
    val dialogManager = LocalDialogManager.current
    val bottomSheetManager = LocalBottomSheetManager.current

    return remember(snackbarManager, dialogManager, bottomSheetManager) {
        val repository = HomeRepositoryImpl()
        val useCase = GetHomeGreetingUseCase(repository)
        HomeViewModel(
            getHomeGreetingUseCase = useCase,
            snackbarManager = snackbarManager,
            dialogManager = dialogManager,
            bottomSheetManager = bottomSheetManager
        ).also { viewModel ->
            homeNavigator?.let(viewModel::setNavigator)
        }
    }
}
