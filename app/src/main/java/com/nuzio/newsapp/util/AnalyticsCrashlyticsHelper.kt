package com.nuzio.newsapp.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object AnalyticsCrashlyticsHelper {

    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var analytics: FirebaseAnalytics

    fun init(crashlytics: FirebaseCrashlytics, analytics: FirebaseAnalytics) {
        this.crashlytics = crashlytics
        this.analytics = analytics
    }

    // 1️⃣ Logging helpers
    fun logMessage(message: String) = crashlytics.log(message)

    fun logEvent(event: String, params: Map<String, Any>? = null) {
        val bundle = Bundle().apply {
            params?.forEach { (k, v) ->
                when (v) {
                    is String -> putString(k, v)
                    is Int -> putInt(k, v)
                    is Long -> putLong(k, v)
                    is Double -> putDouble(k, v)
                    is Float -> putFloat(k, v)
                    is Boolean -> putBoolean(k, v)
                    else -> putString(k, v.toString())
                }
            }
        }
        analytics.logEvent(event, bundle)
    }

    fun safeExecute(tag: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            logMessage("Non-fatal exception: $tag")
            crashlytics.recordException(e)
            logEvent("non_fatal_exception", mapOf("tag" to tag, "message" to (e.message ?: "")))
        }
    }

    fun logNonFatal(message: String) = crashlytics.log(message)

    fun setUserContext(userId: String, email: String?, method: String?) {
        crashlytics.setUserId(userId)
        method?.let { crashlytics.setCustomKey("login_method", it) }
        email?.let { crashlytics.setCustomKey("user_email", it) }
    }

    fun setScreen(screenName: String) {
        logMessage("Screen: $screenName")
        logEvent("screen_view", mapOf("screen_name" to screenName))
    }

    fun logBuildInfo(versionName: String, buildType: String, flavor: String, isDebug: Boolean) {
        crashlytics.setCustomKey("version_name", versionName)
        crashlytics.setCustomKey("build_type", buildType)
        crashlytics.setCustomKey("flavor", flavor)
        crashlytics.setCustomKey("env", if (isDebug) "qa" else "prod")
        logEvent("app_started", mapOf("version_name" to versionName, "build_type" to buildType, "flavor" to flavor))
    }
}
