package com.nuzio.newsapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nuzio.newsapp.utils.AnalyticsCrashlyticsHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class serving as the entry point for the Nuzio News application.
 *
 * Responsibilities include initializing third-party SDKs, configuring analytics
 * and crash reporting, and providing custom image loading configuration through
 * Coil's ImageLoaderFactory interface.
 *
 * The @HiltAndroidApp annotation triggers Hilt's code generation and sets up
 * the application-level dependency injection container that provides dependencies
 * throughout the application lifecycle.
 */
@HiltAndroidApp
class NuzioNewsApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        initializeTimber()
        initializeFacebookSdk()
        initializeFirebase()
        configureEnvironment()

        Timber.d("🚀 Nuzio News Application initialization complete")
    }

    /**
     * Initializes Timber logging framework for debug builds.
     *
     * Timber provides a clean logging API with automatic tagging and
     * supports different logging implementations for debug and release builds.
     * In debug builds, logs output to Logcat for development visibility.
     * In release builds, no logging tree is planted to prevent log pollution.
     */
    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("🌲 Timber initialized for debug logging")
        }
    }

    /**
     * Initializes the Facebook SDK for social authentication integration.
     *
     * Configures the application ID and client token from string resources,
     * initializes the SDK with the application context, and activates app
     * events logging for analytics tracking of user interactions with
     * Facebook authentication flows.
     */
    private fun initializeFacebookSdk() {
        try {
            FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
            FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
            FacebookSdk.sdkInitialize(applicationContext)
            AppEventsLogger.activateApp(this)
            Timber.d("✅ Facebook SDK initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to initialize Facebook SDK")
        }
    }

    /**
     * Initializes Firebase services including Analytics and Crashlytics.
     *
     * Sets up Firebase Analytics for tracking user behavior and engagement
     * metrics, configures Crashlytics for crash reporting and error tracking,
     * and initializes the custom analytics helper that provides centralized
     * logging functionality across both services.
     */
    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)

            val crashlytics = FirebaseCrashlytics.getInstance()
            val analytics = FirebaseAnalytics.getInstance(this)

            AnalyticsCrashlyticsHelper.init(crashlytics, analytics)

            AnalyticsCrashlyticsHelper.logBuildInfo(
                versionName = BuildConfig.VERSION_NAME,
                buildType = BuildConfig.BUILD_TYPE,
                flavor = "default",
                isDebug = BuildConfig.DEBUG
            )

            Timber.d("✅ Firebase initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to initialize Firebase")
        }
    }

    /**
     * Configures environment-specific settings for crash reporting.
     *
     * Sets custom keys in Crashlytics to differentiate between QA and
     * production environments in crash reports, enabling easier filtering
     * and analysis of issues based on the deployment environment.
     */
    private fun configureEnvironment() {
        try {
            val environment = if (BuildConfig.DEBUG) "qa" else "prod"
            FirebaseCrashlytics.getInstance().setCustomKey("env", environment)
            Timber.d("🔧 Environment configured: $environment")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to configure environment")
        }
    }

    /**
     * Provides custom ImageLoader configuration for Coil image loading library.
     *
     * Implements ImageLoaderFactory interface to supply a customized ImageLoader
     * that enables crossfade animations when loading images throughout the
     * application. This configuration applies globally to all Coil image
     * loading operations, ensuring consistent visual behavior.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .crossfade(300) // 300ms crossfade duration for smooth transitions
            .build()
    }
}