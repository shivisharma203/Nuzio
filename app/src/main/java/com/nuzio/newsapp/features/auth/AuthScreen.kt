package com.nuzio.newsapp.features.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nuzio.newsapp.R

/**
 * Authentication screen supporting email/password, Google, and Facebook login.
 *
 * Implements Material Design 3 principles with proper state management through
 * the AuthViewModel, providing a polished authentication experience that
 * integrates seamlessly with Firebase authentication services.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    facebookCallbackManager: CallbackManager,
    viewModel: AuthViewModel = hiltViewModel(),
    onSignInSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            onSignInSuccess()
        }
    }

    LaunchedEffect(Unit) {
        LoginManager.getInstance().registerCallback(
            facebookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    viewModel.signInWithFacebook(result.accessToken.token)
                }

                override fun onCancel() {
                    Toast.makeText(context, "Facebook login cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(
                        context,
                        "Facebook error: ${error.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { viewModel.signInWithGoogle(it) }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Google Sign-In failed: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.getTitle(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onEvent(AuthEvent.SubmitEmailPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.isFormValid()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(state.getButtonText())
                }
            }

            TextButton(
                onClick = { viewModel.onEvent(AuthEvent.ForgotPasswordClicked) },
                enabled = !state.isLoading
            ) {
                Text("Forgot Password?")
            }

            TextButton(
                onClick = { viewModel.onEvent(AuthEvent.ToggleAuthMode) },
                enabled = !state.isLoading
            ) {
                Text(state.getTogglePrompt())
            }

            state.errorMessage?.let { error ->
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Divider(Modifier.padding(vertical = 24.dp))

            OutlinedButton(
                onClick = {
                    viewModel.onEvent(AuthEvent.GoogleSignInClicked)
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val client = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(client.signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("Continue with Google")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    viewModel.onEvent(AuthEvent.FacebookSignInClicked)
                    activity?.let {
                        LoginManager.getInstance().logInWithReadPermissions(
                            it,
                            listOf("email", "public_profile")
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("Continue with Facebook")
            }
        }
    }

    if (state.showForgotPasswordDialog) {
        ForgotPasswordDialog(
            onDismiss = { viewModel.onEvent(AuthEvent.DismissForgotPasswordDialog) },
            onSendReset = { email ->
                viewModel.onEvent(AuthEvent.SendPasswordResetEmail(email))
                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSendReset: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSendReset(email.trim())
                    onDismiss()
                },
                enabled = email.isNotBlank()
            ) {
                Text("Send Reset Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Reset Password") },
        text = {
            Column {
                Text("Enter your email address to receive a password reset link.")
                Spacer(modifier = Modifier.height(16.dp))
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