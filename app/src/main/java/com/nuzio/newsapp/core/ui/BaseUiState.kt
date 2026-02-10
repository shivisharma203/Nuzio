package com.nuzio.newsapp.core.ui

/**
 * Base UI state for all screens.
 *
 * Screens can extend this class to add feature-specific fields.
 *
 * Shared properties:
 * - isLoading: initial loading
 * - isRefreshing: pull-to-refresh or background refresh
 * - errorMessage: network or other errors
 */
open class BaseUiState(
    open val isLoading: Boolean = false,
    open val isRefreshing: Boolean = false,
    open val errorMessage: String? = null
)
