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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isSignedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

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

    fun signOut() {
        viewModelScope.launch {
            try {
                val currentUser = firebaseAuth.currentUser
                val providerId = currentUser?.providerData?.getOrNull(1)?.providerId

                Log.d("SignOut", "Current provider: $providerId")

                // Firebase sign out
                firebaseAuth.signOut()

                // Facebook logout
                if (providerId == FacebookAuthProvider.PROVIDER_ID) {
                    LoginManager.getInstance().logOut()
                }

                // Google logout
                if (providerId == GoogleAuthProvider.PROVIDER_ID) {
                    val googleSignInClient = GoogleSignIn.getClient(
                        context,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("AIzaSyCdv_lejFLZ40P3m5KkgrsmRKJI5_XH3BI") // Replace with your real web client ID
                            .requestEmail()
                            .build()
                    )
                    googleSignInClient.signOut()
                }

                Log.d("AuthViewModel", "✅ Successfully signed out")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "❌ Sign out failed: ${e.localizedMessage}", e)
            }
        }
    }

    // -------------------------
    // Existing methods unchanged
    // -------------------------

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

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception ?: Exception("Google sign-in failed"))
            }
    }

    fun signInWithFacebook(
        accessToken: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = FacebookAuthProvider.getCredential(accessToken)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FacebookLogin", "signInWithFacebook:success")
                    onSuccess()
                } else {
                    Log.e("FacebookLogin", "signInWithFacebook:failure", task.exception)
                    onError(task.exception ?: Exception("Unknown Facebook sign-in error"))
                }
            }
    }


    fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null

    fun checkCurrentUser() {
        _isSignedIn.value = firebaseAuth.currentUser != null
    }
}
