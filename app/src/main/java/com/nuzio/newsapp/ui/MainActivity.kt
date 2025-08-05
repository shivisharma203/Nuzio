package com.nuzio.newsapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.facebook.CallbackManager
import com.google.firebase.FirebaseApp
import com.nuzio.newsapp.ui.screens.NewsListScreen
import com.nuzio.newsapp.ui.screens.SocialLoginScreen
import com.nuzio.newsapp.ui.theme.NuzioTheme
import com.nuzio.newsapp.ui.viewmodel.AuthViewModel
import com.nuzio.newsapp.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: NewsViewModel by viewModels()
    private val facebookCallbackManager = CallbackManager.Factory.create()

    private val callbackManager by lazy { com.facebook.CallbackManager.Factory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)

        setContent {
            NuzioTheme {
                val isSignedIn by authViewModel.isSignedIn.collectAsState()

                if (isSignedIn) {
                    NewsListScreen(
                        viewModel = viewModel,
                        authViewModel=authViewModel,
                        onLogoutSuccess = {
                            lifecycleScope.launch {
                                try {
                                    // Call signOut on ViewModel, which should handle Firebase sign out internally
                                    authViewModel.signOut()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                } else {
                    SocialLoginScreen(
                        facebookCallbackManager = facebookCallbackManager,
                        onSignInSuccess = {

                            authViewModel.checkCurrentUser()  // AuthStateListener updates UI
                        }
                    )
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data) //
    }

}
