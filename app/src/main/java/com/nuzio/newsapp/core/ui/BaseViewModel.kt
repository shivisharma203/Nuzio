package com.nuzio.newsapp.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel to simplify StateFlow handling.
 *
 * Provides:
 * - StateFlow management for UI state
 * - Safe coroutine launching
 * - setState utility for immutably updating state
 *
 * Usage:
 * class MyViewModel : BaseViewModel<MyUiState>(MyUiState()) { ... }
 */
open class BaseViewModel<T : BaseUiState>(
    initialState: T
) : ViewModel() {

    // Backing mutable state
    private val _uiState = MutableStateFlow(initialState)

    // Exposed immutable state
    val uiState: StateFlow<T> = _uiState

    /**
     * Launch a coroutine in ViewModel scope safely.
     */
    protected fun launchState(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    /**
     * Update the state immutably.
     *
     * Example:
     * setState { copy(isLoading = true) }
     */
    protected fun setState(reducer: T.() -> T) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * Get current state value.
     */
    protected fun currentState(): T = _uiState.value
}
