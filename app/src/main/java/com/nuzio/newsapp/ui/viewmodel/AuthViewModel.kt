package com.nuzio.newsapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for handling user authentication logic using Firebase Auth,
 * Google Sign-In, and Facebook Login.
 *
 * It exposes sign-in state as [StateFlow] and provides methods to
 * sign up, login, logout, reset password, and sign in with social providers.
 *
 * @param application Application context required for GoogleSignInClient.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AuthViewModel"

    private val context = getApplication<Application>().applicationContext
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Tracks if user is currently signed in
    private val _isSignedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    // Listener to update signed-in state when auth state changes
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _isSignedIn.value = auth.currentUser != null
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    /**
     * Signs out the currently logged-in user from Firebase,
     * and also logs out from Facebook and Google if applicable.
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                val currentUser = firebaseAuth.currentUser
                val providerId = currentUser?.providerData?.getOrNull(1)?.providerId

                Log.d(TAG, "Current provider: $providerId")

                // Sign out from Firebase
                firebaseAuth.signOut()

                // Facebook logout if user signed in via Facebook
                if (providerId == FacebookAuthProvider.PROVIDER_ID) {
                    LoginManager.getInstance().logOut()
                    Log.d(TAG, "Logged out from Facebook")
                }

                // Google logout if user signed in via Google
                if (providerId == GoogleAuthProvider.PROVIDER_ID) {
                    val googleSignInClient = GoogleSignIn.getClient(
                        context,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            // TODO: Replace with your real Web client ID from Firebase console
                            .requestIdToken("AIzaSyCdv_lejFLZ40P3m5KkgrsmRKJI5_XH3BI")
                            .requestEmail()
                            .build()
                    )
                    googleSignInClient.signOut().addOnCompleteListener {
                        Log.d(TAG, "Logged out from Google")
                    }
                }

                Log.d(TAG, "✅ Successfully signed out")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Sign out failed: ${e.localizedMessage}", e)
            }
        }
    }

    /**
     * Signs up a new user using email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @param onSuccess Callback invoked on successful signup.
     * @param onError Callback invoked with an Exception if signup fails.
     */
    fun signUpWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception ?: Exception("Signup failed"))
            }
    }

    /**
     * Logs in an existing user using email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @param onSuccess Callback invoked on successful login.
     * @param onError Callback invoked with an Exception if login fails.
     */
    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception ?: Exception("Login failed"))
            }
    }

    /**
     * Sends a password reset email to the given email address.
     *
     * @param email Email address to send the reset link.
     * @param onSuccess Callback invoked when email is successfully sent.
     * @param onError Callback invoked with an Exception if sending fails.
     */
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception ?: Exception("Failed to send reset email"))
            }
    }

    /**
     * Signs in user with Google credentials.
     *
     * @param idToken Google ID token from GoogleSignInClient.
     * @param onSuccess Callback invoked on successful sign-in.
     * @param onError Callback invoked with an Exception if sign-in fails.
     */
    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception ?: Exception("Google sign-in failed"))
            }
    }

    /**
     * Signs in user with Facebook credentials.
     *
     * @param accessToken Facebook access token.
     * @param onSuccess Callback invoked on successful sign-in.
     * @param onError Callback invoked with an Exception if sign-in fails.
     */
    fun signInWithFacebook(
        accessToken: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = FacebookAuthProvider.getCredential(accessToken)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Facebook sign-in successful")
                    onSuccess()
                } else {
                    Log.e(TAG, "Facebook sign-in failed", task.exception)
                    onError(task.exception ?: Exception("Unknown Facebook sign-in error"))
                }
            }
    }

    /** Returns true if a user is currently signed in. */
    fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null

    /** Manually update the isSignedIn StateFlow based on current user state. */
    fun checkCurrentUser() {
        _isSignedIn.value = firebaseAuth.currentUser != null
    }
}
