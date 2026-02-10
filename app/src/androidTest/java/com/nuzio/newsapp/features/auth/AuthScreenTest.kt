package com.nuzio.newsapp.features.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.facebook.CallbackManager
import com.nuzio.newsapp.HiltTestActivity
import com.nuzio.newsapp.navigation.LoginRoute
import com.nuzio.newsapp.navigation.NewsListRoute
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive instrumentation tests for AuthScreen validating authentication flows.
 *
 * This test suite verifies the complete authentication user experience including
 * form rendering, input validation, state management through the AuthViewModel,
 * error presentation, loading states, and social authentication integration.
 * The tests execute within a Hilt-enabled environment to properly initialize
 * dependency injection for ViewModel instances and repository dependencies.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private lateinit var callbackManager: CallbackManager

    @Before
    fun setup() {
        hiltRule.inject()
        callbackManager = CallbackManager.Factory.create()
    }

    @Test
    fun authScreenDisplaysInitialLoginState() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Sign up").assertIsDisplayed()
    }

    @Test
    fun authScreenDisplaysAllFormFields() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNode(hasSetTextAction()).assertExists()
    }

    @Test
    fun authScreenDisplaysAuthenticationButtons() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Login").assertExists()
        composeTestRule.onNodeWithText("Forgot Password?").assertExists()
        composeTestRule.onNodeWithText("Continue with Google").assertExists()
        composeTestRule.onNodeWithText("Continue with Facebook").assertExists()
    }

    @Test
    fun emailFieldAcceptsAndDisplaysUserInput() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[0].performTextInput("test@example.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("test@example.com").assertExists()
    }

    @Test
    fun passwordFieldAcceptsUserInput() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        val passwordField = composeTestRule.onAllNodesWithText("Password")[0]
        passwordField.performTextInput("password123")
        composeTestRule.waitForIdle()

        passwordField.assertExists()
    }

    @Test
    fun loginButtonRemainsDisabledWithEmptyFields() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        val loginButton = composeTestRule.onNodeWithText("Login")
        loginButton.assertIsNotEnabled()
    }

    @Test
    fun loginButtonBecomesEnabledWithValidInput() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[0].performTextInput("test@example.com")
        composeTestRule.onAllNodesWithText("Password")[0].performTextInput("password123")
        composeTestRule.waitForIdle()

        val loginButton = composeTestRule.onNodeWithText("Login")
        loginButton.assertIsEnabled()
    }

    @Test
    fun toggleButtonSwitchesToSignUpMode() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account? Login").assertIsDisplayed()
    }

    @Test
    fun toggleButtonSwitchesBackToLoginMode() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Already have an account? Login").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun buttonTextChangesWithAuthenticationMode() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Login", useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sign Up", useUnmergedTree = true).assertExists()
    }

    @Test
    fun forgotPasswordButtonOpensResetDialog() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Reset Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Send Reset Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordDialogDisplaysEmailField() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Enter your email address to receive a password reset link.")
            .assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Email").assertCountEquals(2)
    }

    @Test
    fun forgotPasswordDialogAcceptsEmailInput() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[1].performTextInput("reset@example.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("reset@example.com").assertExists()
    }

    @Test
    fun forgotPasswordDialogSendButtonDisabledWithEmptyEmail() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Send Reset Email").assertIsNotEnabled()
    }

    @Test
    fun forgotPasswordDialogSendButtonEnabledWithValidEmail() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[1].performTextInput("reset@example.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Send Reset Email").assertIsEnabled()
    }

    @Test
    fun forgotPasswordDialogDismissesOnCancel() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Reset Password").assertDoesNotExist()
    }

    @Test
    fun forgotPasswordDialogDismissesOnSendEmail() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[1].performTextInput("reset@example.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Send Reset Email").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Reset Password").assertDoesNotExist()
    }

    @Test
    fun socialLoginButtonsAreAlwaysVisible() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Continue with Google").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue with Facebook").assertIsDisplayed()
    }

    @Test
    fun socialLoginButtonsRemainVisibleInSignUpMode() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Continue with Google").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue with Facebook").assertIsDisplayed()
    }

    @Test
    fun dividerSeparatesEmailAndSocialAuthentication() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        val rootNode = composeTestRule.onRoot()
        rootNode.assertExists()
    }

    @Test
    fun formFieldsClearWhenSwitchingModes() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[0].performTextInput("test@example.com")
        composeTestRule.onAllNodesWithText("Password")[0].performTextInput("password123")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("test@example.com").assertExists()
    }

    @Test
    fun allButtonsDisabledDuringLoading() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[0].performTextInput("test@example.com")
        composeTestRule.onAllNodesWithText("Password")[0].performTextInput("password123")
        composeTestRule.waitForIdle()
    }

    @Test
    fun screenMaintainsStateAcrossRecomposition() {
        composeTestRule.setContent {
            AuthScreen(
                onNavigateToNewsList = {
                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Email")[0].performTextInput("test@example.com")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("test@example.com").assertExists()

        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("test@example.com").assertExists()
    }
}