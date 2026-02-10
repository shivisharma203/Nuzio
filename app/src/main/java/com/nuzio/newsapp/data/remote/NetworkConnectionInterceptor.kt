package com.nuzio.newsapp.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Exception thrown when network operations are attempted without connectivity.
 *
 * This custom exception type allows calling code to distinguish between
 * connectivity failures and other types of network errors, enabling
 * appropriate user feedback for offline scenarios.
 */
class NoConnectivityException : IOException() {
    override val message: String
        get() = "No network connection available. Please check your internet connection and try again."
}

/**
 * OkHttp interceptor that verifies network connectivity before allowing requests.
 *
 * This interceptor prevents unnecessary network operations when the device lacks
 * connectivity, throwing NoConnectivityException immediately rather than waiting
 * for inevitable timeout failures. The implementation checks both network availability
 * and actual internet capability to distinguish between connected networks without
 * internet access and genuine connectivity.
 */
class NetworkConnectionInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            Timber.w("📵 Network request blocked: No connectivity available")
            throw NoConnectivityException()
        }

        return chain.proceed(chain.request())
    }

    /**
     * Checks whether network connectivity is currently available.
     *
     * This implementation uses the modern NetworkCapabilities API available
     * from Android M onwards, providing more accurate connectivity information
     * than the legacy ConnectivityManager methods. The check verifies both
     * that a network is connected and that it has internet capability, preventing
     * false positives from captive portals or local networks without internet access.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}