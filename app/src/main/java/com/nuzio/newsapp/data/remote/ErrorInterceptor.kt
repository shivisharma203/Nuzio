package com.nuzio.newsapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Custom exception for API-specific errors with user-friendly messages.
 *
 * This exception type carries both the HTTP status code for programmatic
 * handling and a user-friendly error message suitable for display in the UI.
 */
class ApiException(
    val statusCode: Int,
    override val message: String
) : IOException(message)

/**
 * OkHttp interceptor that transforms HTTP error responses into meaningful exceptions.
 *
 * This interceptor examines response codes and extracts error information from
 * response bodies, converting technical HTTP errors into user-comprehensible
 * messages. The implementation provides centralized error handling that eliminates
 * duplicate error parsing logic across repository implementations while ensuring
 * consistent error messaging throughout the application.
 */
class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Allow successful responses to pass through unchanged
        if (response.isSuccessful) {
            return response
        }

        // Extract and parse error information
        val errorMessage = parseErrorMessage(response)
        val statusCode = response.code

        Timber.e("❌ API Error [${statusCode}]: $errorMessage")
        Timber.d("Failed request: ${request.method} ${request.url}")

        // Throw custom exception with user-friendly message
        throw ApiException(statusCode, errorMessage)
    }

    /**
     * Parses error responses to extract meaningful error messages.
     *
     * This implementation attempts to extract error messages from response bodies
     * when available, falling back to standard HTTP status descriptions when
     * specific error details are unavailable. The News API typically returns
     * JSON error responses, but this implementation handles both JSON and plain
     * text error formats gracefully.
     */
    private fun parseErrorMessage(response: Response): String {
        val statusCode = response.code

        return when (statusCode) {
            401 -> "Invalid API key. Please check your configuration."
            403 -> "Access forbidden. Your API key may not have sufficient permissions."
            404 -> "The requested resource was not found."
            426 -> "API key upgrade required. You have exceeded the free tier limits."
            429 -> "Too many requests. Please try again later."
            500, 502, 503, 504 -> "The news service is temporarily unavailable. Please try again later."
            else -> {
                // Attempt to extract error message from response body
                try {
                    val errorBody = response.body?.string()
                    if (!errorBody.isNullOrBlank()) {
                        // News API returns JSON errors with a "message" field
                        // Simple extraction without full JSON parsing
                        val messageMatch = Regex(""""message"\s*:\s*"([^"]+)"""").find(errorBody)
                        messageMatch?.groupValues?.getOrNull(1) ?: "An error occurred: HTTP $statusCode"
                    } else {
                        "An error occurred: HTTP $statusCode"
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to parse error response body")
                    "An error occurred: HTTP $statusCode"
                }
            }
        }
    }
}