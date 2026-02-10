package com.nuzio.newsapp.core.network

import com.nuzio.newsapp.data.remote.ApiException
import com.nuzio.newsapp.data.remote.NoConnectivityException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (exception: Exception) {
        Timber.e(exception, "API call failed")

        val errorMessage = when (exception) {
            is NoConnectivityException -> {
                // Connectivity exception already contains user-friendly message
                exception.message ?: "No internet connection available"
            }

            is ApiException -> {
                // API exception already contains parsed error message
                exception.message
            }

            is HttpException -> {
                // Generic HTTP errors not handled by ErrorInterceptor
                when (exception.code()) {
                    400 -> "Bad request. Please check your input and try again."
                    500 -> "Server error. Please try again later."
                    else -> "Network error: ${exception.message()}"
                }
            }

            is SocketTimeoutException -> {
                "Connection timeout. Please check your internet connection and try again."
            }

            is UnknownHostException -> {
                "Unable to reach the server. Please check your internet connection."
            }

            is IOException -> {
                "Network error occurred. Please try again."
            }

            else -> {
                "An unexpected error occurred: ${exception.localizedMessage}"
            }
        }

        Resource.Error(exception, errorMessage)
    }
}