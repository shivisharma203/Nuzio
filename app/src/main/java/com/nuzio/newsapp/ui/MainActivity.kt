package com.nuzio.newsapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.facebook.CallbackManager
import com.google.firebase.FirebaseApp
import com.nuzio.newsapp.data.model.NewsItem
import com.nuzio.newsapp.ui.screens.NewsDetailScreen
import com.nuzio.newsapp.ui.screens.NewsListScreen
import com.nuzio.newsapp.ui.screens.SocialLoginScreen
import com.nuzio.newsapp.ui.theme.NuzioTheme
import com.nuzio.newsapp.ui.viewmodel.AuthViewModel
import com.nuzio.newsapp.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.net.Uri
import com.google.gson.Gson

// ----------------------
// Navigation Routes Enum
// ----------------------
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object NewsList : Screen("news_list")


}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val facebookCallbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    @SuppressLint("ComposableDestinationInComposeScope")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(applicationContext)

        setContent {
            NuzioTheme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = hiltViewModel()
                val isSignedIn by authViewModel.isSignedIn.collectAsState()

                var startDestination by remember { mutableStateOf(Screen.Login.route) }
                LaunchedEffect(isSignedIn) {
                    startDestination = if (isSignedIn) Screen.NewsList.route else Screen.Login.route
                }

                NavHost(navController = navController, startDestination = startDestination) {

                    // Login screen
                    composable(Screen.Login.route) {
                        val loginAuthViewModel: AuthViewModel = hiltViewModel()
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally { it } + fadeIn(),
                            exit = slideOutHorizontally { -it } + fadeOut()
                        ) {
                            SocialLoginScreen(
                                facebookCallbackManager = facebookCallbackManager,
                                authViewModel = loginAuthViewModel,
                                onSignInSuccess = {
                                    loginAuthViewModel.checkCurrentUser()
                                    navController.navigate(Screen.NewsList.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }

                    // News List screen
                    composable(Screen.NewsList.route) {
                        val newsViewModel: NewsViewModel = hiltViewModel()
                        val newsAuthViewModel: AuthViewModel = hiltViewModel()

                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally { it } + fadeIn(),
                            exit = slideOutHorizontally { -it } + fadeOut()
                        ) {
                            NewsListScreen(
                                viewModel = newsViewModel,
                                authViewModel = newsAuthViewModel,
                                onLogoutSuccess = {
                                    lifecycleScope.launch {
                                        try {
                                            newsAuthViewModel.signOut()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.NewsList.route) { inclusive = true }
                                    }
                                },
                                onNewsItemClick = { newsItem ->
                                    val newsJson = Uri.encode(Gson().toJson(newsItem))
                                    navController.navigate("news_detail/$newsJson")
                                }
                            )
                        }
                    }

                    // News Detail screen — must be **direct child** of NavHost, NOT nested inside NewsList
                    composable(
                        route = "news_detail/{newsJson}",
                        arguments = listOf(navArgument("newsJson") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val json = backStackEntry.arguments?.getString("newsJson")
                        val newsItem = Gson().fromJson(json, NewsItem::class.java)
                        NewsDetailScreen(
                            newsItem = newsItem,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}