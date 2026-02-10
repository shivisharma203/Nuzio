package com.nuzio.newsapp.features.auth

import com.nuzio.newsapp.core.ui.BaseUiState


/**
 * Represents the complete state of the authentication screen.
 *
 * This immutable data class captures all possible configurations including
 * form field values, loading states, error messages, and mode selection,
 * enabling predictable state management and comprehensive testing coverage.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val showForgotPasswordDialog: Boolean = false,
    val isAuthenticated: Boolean = false,
    override val isRefreshing: Boolean = false
) : BaseUiState(isLoading, isRefreshing, errorMessage) {

    /**
     * Validates whether the form contains sufficient data for submission.
     * Both email and password must contain non-blank values.
     */
    fun isFormValid(): Boolean = email.isNotBlank() && password.isNotBlank()

    /**
     * Returns the appropriate title based on the current authentication mode.
     */
    fun getTitle(): String = if (isLoginMode) "Sign In" else "Sign Up"

    /**
     * Returns the appropriate button text based on the current mode.
     */
    fun getButtonText(): String = if (isLoginMode) "Login" else "Sign Up"

    /**
     * Returns the appropriate toggle prompt based on the current mode.
     */
    fun getTogglePrompt(): String = if (isLoginMode)
        "Don't have an account? Sign up"
    else
        "Already have an account? Login"
}