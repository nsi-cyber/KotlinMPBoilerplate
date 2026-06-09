package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation

/**
 * Home feature event definitions used by the MVI reducer.
 */
sealed interface HomeEvent {
    data object OnLoadGreetingClicked : HomeEvent
    data object OnShowSnackbarClicked : HomeEvent
    data object OnShowDialogClicked : HomeEvent
    data object OnShowBottomSheetClicked : HomeEvent
    data object OnNavigateDemoClicked : HomeEvent
}
