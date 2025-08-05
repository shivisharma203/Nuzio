package com.nuzio.newsapp.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nuzio.newsapp.R
import com.nuzio.newsapp.ui.viewmodel.AuthViewModel

@Composable
fun SocialLoginScreen(
    facebookCallbackManager: CallbackManager,
    authViewModel: AuthViewModel = viewModel(),
    onSignInSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var currentErrorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }

    // Facebook callback registration
    LaunchedEffect(Unit) {
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    isLoading = true
                    authViewModel.signInWithFacebook(
                        accessToken = result.accessToken.token,
                        onSuccess = {
                            isLoading = false
                            onSignInSuccess()
                        },
                        onError = { error ->
                            isLoading = false
                            currentErrorMessage = "Facebook Sign-In Error: ${error.message}"
                        }
                    )
                }

                override fun onCancel() {
                    currentErrorMessage = "Facebook login cancelled"
                }

                override fun onError(error: FacebookException) {
                    currentErrorMessage = "Facebook login error: ${error.localizedMessage}"
                }
            }
        )
    }

    // Check if already signed in
    LaunchedEffect(Unit) {
        if (authViewModel.isUserSignedIn()) {
            onSignInSuccess()
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isLoading = true
        currentErrorMessage = null
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.signInWithGoogle(
                idToken = account.idToken!!,
                onSuccess = {
                    isLoading = false
                    onSignInSuccess()
                },
                onError = { error ->
                    isLoading = false
                    currentErrorMessage = "Google Sign-In Error: ${error.message}"
                }
            )
        } catch (e: Exception) {
            isLoading = false
            currentErrorMessage = "Google Sign-In failed: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLoginMode) "Login with Email" else "Create an Account",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                currentErrorMessage = null

                if (email.isBlank() || password.isBlank()) {
                    currentErrorMessage = "Email and Password are required."
                    isLoading = false
                    return@Button
                }

                if (isLoginMode) {
                    authViewModel.loginWithEmail(
                        email, password,
                        onSuccess = {
                            isLoading = false
                            onSignInSuccess()
                        },
                        onError = { e ->
                            isLoading = false
                            currentErrorMessage = "Login Failed: ${e.message}"
                        }
                    )
                } else {
                    authViewModel.signUpWithEmail(
                        email, password,
                        onSuccess = {
                            isLoading = false
                            onSignInSuccess()
                        },
                        onError = { e ->
                            isLoading = false
                            currentErrorMessage = "Signup Failed: ${e.message}"
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoginMode) "Login" else "Sign Up")
        }

        TextButton(onClick = { showForgotDialog = true }) {
            Text("Forgot Password?")
        }

        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Login")
        }

        Spacer(Modifier.height(8.dp))

        currentErrorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Divider(Modifier.padding(vertical = 16.dp))

        // --- Google Sign-In Button ---
        Button(
            onClick = {
                isLoading = true
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val client = GoogleSignIn.getClient(context, options)
                googleSignInLauncher.launch(client.signInIntent)
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue with Google")
        }

        Spacer(Modifier.height(8.dp))

        // --- Facebook Sign-In Button ---
        Button(
            onClick = {
                currentErrorMessage = null
                isLoading = true
                if (activity != null) {
                    LoginManager.getInstance().logInWithReadPermissions(
                        activity,
                        listOf("email", "public_profile")
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue with Facebook")
        }
    }

    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onSendReset = { emailInput ->
                authViewModel.sendPasswordResetEmail(
                    email = emailInput,
                    onSuccess = {
                        Toast.makeText(context, "Reset link sent", Toast.LENGTH_SHORT).show()
                    },
                    onError = { e ->
                        Toast.makeText(context, e.message ?: "Failed", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSendReset: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSendReset(email.trim())
                onDismiss()
            }) {
                Text("Send Reset Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Forgot Password?") },
        text = {
            Column {
                Text("Enter your email to receive a password reset link.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
