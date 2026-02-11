package com.nuzio.newsapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.FacebookSdk
import com.facebook.CallbackManager
import com.nuzio.newsapp.core.theme.NuzioTheme
import com.nuzio.newsapp.navigation.AppNavGraph

import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var facebookCallbackManager: CallbackManager

    // 👇 ADD NOTIFICATION PERMISSION LAUNCHER
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.d("Notification permission granted")
        } else {
            Timber.d("Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)
        facebookCallbackManager = CallbackManager.Factory.create()

        // 👇 REQUEST NOTIFICATION PERMISSION ON ANDROID 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            NuzioTheme {
                AppNavGraph(
                    facebookCallbackManager = facebookCallbackManager
                )
            }
        }
    }
}