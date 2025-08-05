package com.nuzio.newsapp.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.material3.MaterialTheme

/**
 * Main social login screen supporting email/password, Google, and Facebook login.
 */
@Composable
fun SocialLoginScreen(
    facebookCallbackManager: CallbackManager,
    authViewModel: AuthViewModel = viewModel(),
    onSignInSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // State variables for form fields, UI state, and errors
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var currentErrorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }

    // Combine Facebook callback registration and signed-in check in one effect
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

        // Check user already signed in
        if (authViewModel.isUserSignedIn()) {
            onSignInSuccess()
        }
    }

    // Google sign-in launcher and handler
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

    // Main UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLoginMode) stringResource(R.string.login_title) else stringResource(R.string.signup_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        // Email input field
        EmailField(value = email, onValueChange = { email = it })

        Spacer(Modifier.height(8.dp))

        // Password input field
        PasswordField(value = password, onValueChange = { password = it })

        Spacer(Modifier.height(16.dp))

        // Login/Signup button
        Button(
            onClick = {
                isLoading = true
                currentErrorMessage = null

                if (email.isBlank() || password.isBlank()) {
                    currentErrorMessage = context.getString(R.string.error_email_password_required)
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
                            currentErrorMessage = context.getString(R.string.login_failed, e.message)
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
                            currentErrorMessage = context.getString(R.string.signup_failed, e.message)
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoginMode) stringResource(R.string.login) else stringResource(R.string.signup))
        }

        // Forgot password button
        TextButton(onClick = { showForgotDialog = true }) {
            Text(stringResource(R.string.forgot_password))
        }

        // Toggle between login/signup modes
        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(
                if (isLoginMode) stringResource(R.string.prompt_signup)
                else stringResource(R.string.prompt_login)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Display error message if any
        currentErrorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Divider(Modifier.padding(vertical = 16.dp))

        // Google login button
        GoogleLoginButton(
            isLoading = isLoading,
            onClick = {
                isLoading = true
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val client = GoogleSignIn.getClient(context, options)
                googleSignInLauncher.launch(client.signInIntent)
            }
        )

        Spacer(Modifier.height(8.dp))

        // Facebook login button
        FacebookLoginButton(
            isLoading = isLoading,
            onClick = {
                currentErrorMessage = null
                isLoading = true
                activity?.let {
                    LoginManager.getInstance().logInWithReadPermissions(
                        it,
                        listOf("email", "public_profile")
                    )
                }
            }
        )
    }

    // Forgot password dialog
    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onSendReset = { emailInput ->
                authViewModel.sendPasswordResetEmail(
                    email = emailInput,
                    onSuccess = {
                        Toast.makeText(context, context.getString(R.string.reset_email_sent), Toast.LENGTH_SHORT).show()
                    },
                    onError = { e ->
                        Toast.makeText(context, e.message ?: context.getString(R.string.failed), Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}

/**
 * Email input field with Material3 theming and colors.
 */
@Composable
fun EmailField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.email)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
       /* colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )*/
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEmailField() {
    EmailField(value = "", onValueChange = {})
}

/**
 * Password input field with visual transformation and theming.
 */
@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.password)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
       /* colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )*/
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordField() {
    PasswordField(value = "", onValueChange = {})
}

/**
 * Button to initiate Google sign-in.
 */
@Composable
fun GoogleLoginButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.continue_with_google))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoogleLoginButton() {
    GoogleLoginButton(isLoading = false, onClick = {})
}

/**
 * Button to initiate Facebook sign-in.
 */
@Composable
fun FacebookLoginButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.continue_with_facebook))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFacebookLoginButton() {
    FacebookLoginButton(isLoading = false, onClick = {})
}

/**
 * Forgot password dialog prompting for email input.
 */
@Composable
fun ForgotPasswordDialog(
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
                Text(stringResource(R.string.send_reset_email))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.forgot_password_title)) },
        text = {
            Column {
                Text(stringResource(R.string.forgot_password_instruction))
                Spacer(modifier = Modifier.height(8.dp))
                EmailField(value = email, onValueChange = { email = it })
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordDialog() {
    ForgotPasswordDialog(onDismiss = {}, onSendReset = {})
}
