package com.nuzio.newsapp.features.auth

/**
 * Sealed class representing all possible user interactions on the auth screen.
 *
 * Using a sealed class ensures type-safety and makes it impossible to forget
 * handling any event type in the ViewModel, providing compile-time guarantees
 * about event processing completeness.
 */
sealed class AuthEvent {

    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data object ToggleAuthMode : AuthEvent()
    data object SubmitEmailPassword : AuthEvent()
    data object GoogleSignInClicked : AuthEvent()
    data object FacebookSignInClicked : AuthEvent()
    data object ForgotPasswordClicked : AuthEvent()
    data object DismissForgotPasswordDialog : AuthEvent()
    data class SendPasswordResetEmail(val email: String) : AuthEvent()
    data object SignOut : AuthEvent()
    data object ClearError : AuthEvent()
}