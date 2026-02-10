package com.nuzio.newsapp.data.remote

import com.nuzio.newsapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * OkHttp interceptor that automatically adds API key authentication to all requests.
 *
 * This interceptor modifies outgoing requests to include the News API key as a query
 * parameter, centralizing authentication logic and eliminating the need to manually
 * pass API keys in service method definitions. The implementation follows clean
 * architecture principles by encapsulating infrastructure concerns within the
 * networking layer rather than exposing them to higher-level components.
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Add API key as query parameter to the request URL
        val urlWithAuth = originalUrl.newBuilder()
            .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
            .build()

        // Build new request with the modified URL
        val authenticatedRequest = originalRequest.newBuilder()
            .url(urlWithAuth)
            .build()

        if (BuildConfig.DEBUG) {
            Timber.d("🔐 Adding authentication to request: ${authenticatedRequest.url}")
        }

        return chain.proceed(authenticatedRequest)
    }
}