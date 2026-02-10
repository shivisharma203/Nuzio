package com.nuzio.newsapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.facebook.CallbackManager
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.features.auth.AuthScreen
import com.nuzio.newsapp.features.auth.AuthViewModel
import com.nuzio.newsapp.features.news.detail.NewsDetailScreen
import com.nuzio.newsapp.features.screens.NewsListScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
object LoginRoute

@Serializable
object NewsListRoute

@Serializable
data class NewsDetailRoute(val articleJson: String)

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    facebookCallbackManager: CallbackManager
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isSignedIn by authViewModel.isSignedIn.collectAsState()

    LaunchedEffect(isSignedIn) {
        Firebase.crashlytics.setCustomKey("user_signed_in", isSignedIn)
    }

    NavHost(
        navController = navController,
        startDestination = if (isSignedIn) NewsListRoute else LoginRoute,
        modifier = modifier
    ) {
        composable<LoginRoute> {
            Firebase.crashlytics.log("Screen: Authentication")

            AuthScreen(
                facebookCallbackManager = facebookCallbackManager,
                viewModel = authViewModel,
                onSignInSuccess = {
                    Firebase.crashlytics.log("Authentication successful")
                    Firebase.crashlytics.setCustomKey("login_timestamp", System.currentTimeMillis())

                    navController.navigate(NewsListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<NewsListRoute> {
            Firebase.crashlytics.log("Screen: News List")

            NewsListScreen(
                onArticleClick = { article ->
                    Firebase.crashlytics.log("Article clicked: ${article.title}")
                    val articleJson = Json.encodeToString(article)
                    navController.navigate(NewsDetailRoute(articleJson))
                }
            )
        }

        composable<NewsDetailRoute> { backStackEntry ->
            Firebase.crashlytics.log("Screen: News Detail")

            val route = backStackEntry.toRoute<NewsDetailRoute>()
            val article = try {
                Json.decodeFromString<NewsArticle>(route.articleJson)
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
                Firebase.crashlytics.log("Failed to deserialize article")
                null
            }

            if (article != null) {
                NewsDetailScreen(
                    article = article,
                    onNavigateBack = { navController.navigateUp() }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigateUp()
                }
            }
        }
    }

    LaunchedEffect(isSignedIn) {
        if (!isSignedIn && navController.currentDestination?.route != LoginRoute::class.qualifiedName) {
            Firebase.crashlytics.log("User signed out - redirecting to login")
            navController.navigate(LoginRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}