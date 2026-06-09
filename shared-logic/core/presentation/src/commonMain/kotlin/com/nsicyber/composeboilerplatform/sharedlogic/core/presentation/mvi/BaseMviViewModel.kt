package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.mvi

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Common MVI base class with optional navigator binding support.
 */
abstract class BaseMviViewModel<STATE, EFFECT, EVENT, NAVIGATOR>(
    initialState: STATE
) {
    private val mutableState = MutableStateFlow(initialState)
    private val mutableEffect = MutableSharedFlow<EFFECT>()

    private var _navigator: NAVIGATOR? = null

    /**
     * Observable immutable state stream for UI rendering.
     */
    val state: StateFlow<STATE> = mutableState.asStateFlow()

    /**
     * One-shot side effects stream.
     */
    val effect: SharedFlow<EFFECT> = mutableEffect.asSharedFlow()

    /**
     * Optional navigator bound by UI host layer.
     */
    protected val navigator: NAVIGATOR?
        get() = _navigator

    /**
     * Handles UI events.
     */
    abstract fun process(event: EVENT)

    /**
     * Binds feature navigator instance to this ViewModel.
     */
    fun setNavigator(navigator: NAVIGATOR) {
        _navigator = navigator
    }

    /**
     * Updates current state atomically.
     */
    protected fun updateState(transform: (STATE) -> STATE) {
        mutableState.update(transform)
    }

    /**
     * Emits one-time effects.
     */
    protected suspend fun emitEffect(effect: EFFECT) {
        mutableEffect.emit(effect)
    }
}
