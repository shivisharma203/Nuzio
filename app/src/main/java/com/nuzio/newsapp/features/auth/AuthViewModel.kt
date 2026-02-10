package com.nuzio.newsapp.features.auth

import android.app.Application
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nuzio.newsapp.R
import com.nuzio.newsapp.core.ui.BaseViewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel managing authentication state and operations.
 *
 * Coordinates Firebase authentication for email/password, Google Sign-In,
 * and Facebook Login while maintaining immutable UI state and providing
 * type-safe event handling through the BaseViewModel pattern.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val application: Application
) : BaseViewModel<AuthUiState>(AuthUiState()) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isSignedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _isSignedIn.value = auth.currentUser != null
        setState { copy(isAuthenticated = auth.currentUser != null) }
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
        checkAuthenticationStatus()
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    /**
     * Handles all user events from the authentication UI.
     */
    fun onEvent(event: AuthEvent) {
        Timber.d("🎯 Auth Event: ${event::class.simpleName}")

        when (event) {
            is AuthEvent.EmailChanged -> {
                setState { copy(email = event.email, errorMessage = null) }
            }

            is AuthEvent.PasswordChanged -> {
                setState { copy(password = event.password, errorMessage = null) }
            }

            is AuthEvent.ToggleAuthMode -> {
                setState {
                    copy(
                        isLoginMode = !isLoginMode,
                        errorMessage = null
                    )
                }
            }

            is AuthEvent.SubmitEmailPassword -> {
                handleEmailPasswordAuth()
            }

            is AuthEvent.GoogleSignInClicked -> {
                setState { copy(isLoading = true, errorMessage = null) }
            }

            is AuthEvent.FacebookSignInClicked -> {
                setState { copy(isLoading = true, errorMessage = null) }
            }

            is AuthEvent.ForgotPasswordClicked -> {
                setState { copy(showForgotPasswordDialog = true) }
            }

            is AuthEvent.DismissForgotPasswordDialog -> {
                setState { copy(showForgotPasswordDialog = false) }
            }

            is AuthEvent.SendPasswordResetEmail -> {
                sendPasswordResetEmail(event.email)
            }

            is AuthEvent.SignOut -> {
                signOut()
            }

            is AuthEvent.ClearError -> {
                setState { copy(errorMessage = null) }
            }
        }
    }

    /**
     * Handles email/password authentication for both login and signup.
     */
    private fun handleEmailPasswordAuth() {
        val state = currentState()

        if (!state.isFormValid()) {
            setState {
                copy(
                    errorMessage = "Email and password are required",
                    isLoading = false
                )
            }
            return
        }

        setState { copy(isLoading = true, errorMessage = null) }

        val task = if (state.isLoginMode) {
            firebaseAuth.signInWithEmailAndPassword(state.email, state.password)
        } else {
            firebaseAuth.createUserWithEmailAndPassword(state.email, state.password)
        }

        task.addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                Timber.d("✅ Email auth successful")
                setState {
                    copy(
                        isLoading = false,
                        isAuthenticated = true,
                        errorMessage = null
                    )
                }
            } else {
                val errorMsg = authTask.exception?.message ?: "Authentication failed"
                Timber.e("❌ Email auth failed: $errorMsg")
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }

    /**
     * Processes Google Sign-In with the provided ID token.
     */
    fun signInWithGoogle(idToken: String) {
        Timber.d("🔐 Processing Google Sign-In")
        setState { copy(isLoading = true, errorMessage = null) }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("✅ Google Sign-In successful")
                    setState {
                        copy(
                            isLoading = false,
                            isAuthenticated = true,
                            errorMessage = null
                        )
                    }
                } else {
                    val errorMsg = task.exception?.message ?: "Google Sign-In failed"
                    Timber.e("❌ Google Sign-In failed: $errorMsg")
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                }
            }
    }

    /**
     * Processes Facebook Sign-In with the provided access token.
     */
    fun signInWithFacebook(accessToken: String) {
        Timber.d("🔐 Processing Facebook Sign-In")
        setState { copy(isLoading = true, errorMessage = null) }

        val credential = FacebookAuthProvider.getCredential(accessToken)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("✅ Facebook Sign-In successful")
                    setState {
                        copy(
                            isLoading = false,
                            isAuthenticated = true,
                            errorMessage = null
                        )
                    }
                } else {
                    val errorMsg = task.exception?.message ?: "Facebook Sign-In failed"
                    Timber.e("❌ Facebook Sign-In failed: $errorMsg")
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                }
            }
    }

    /**
     * Sends password reset email to the specified address.
     */
    private fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            setState { copy(errorMessage = "Please enter your email address") }
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                setState { copy(showForgotPasswordDialog = false) }

                if (task.isSuccessful) {
                    Timber.d("✅ Password reset email sent")
                } else {
                    val errorMsg = task.exception?.message ?: "Failed to send reset email"
                    Timber.e("❌ Password reset failed: $errorMsg")
                    setState { copy(errorMessage = errorMsg) }
                }
            }
    }

    /**
     * Signs out the current user from all authentication providers.
     */
    private fun signOut() {
        launchState {
            try {
                val currentUser = firebaseAuth.currentUser
                val providerId = currentUser?.providerData?.getOrNull(1)?.providerId

                firebaseAuth.signOut()

                when (providerId) {
                    FacebookAuthProvider.PROVIDER_ID -> {
                        LoginManager.getInstance().logOut()
                        Timber.d("📤 Logged out from Facebook")
                    }

                    GoogleAuthProvider.PROVIDER_ID -> {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(application.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()

                        GoogleSignIn.getClient(application, gso)
                            .signOut()
                            .addOnCompleteListener {
                                Timber.d("📤 Logged out from Google")
                            }
                    }
                }

                setState {
                    copy(
                        isAuthenticated = false,
                        email = "",
                        password = "",
                        errorMessage = null
                    )
                }

                Timber.d("✅ Sign out completed successfully")
            } catch (e: Exception) {
                Timber.e(e, "❌ Sign out failed")
                setState { copy(errorMessage = "Sign out failed: ${e.message}") }
            }
        }
    }

    /**
     * Checks current authentication status and updates state accordingly.
     */
    private fun checkAuthenticationStatus() {
        val isAuthenticated = firebaseAuth.currentUser != null
        setState { copy(isAuthenticated = isAuthenticated) }
        Timber.d("🔍 Authentication status: $isAuthenticated")
    }

    /**
     * Utility method for external authentication status checks.
     */
    fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null
}