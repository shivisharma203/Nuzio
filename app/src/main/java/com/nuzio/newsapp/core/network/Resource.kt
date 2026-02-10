package com.nuzio.newsapp.core.network

/**
 * A sealed class representing the state of an operation result.
 *
 * This class provides a type-safe way to represent success, error, and loading states
 * throughout the application. It is used as a wrapper for all data operations including
 * API calls, database operations, and business logic results.
 *
 * ## Usage Example:
 * ```kotlin
 * when (val result = repository.getNews()) {
 *     is Resource.Success -> {
 *         val news = result.data
 *         // Display news to user
 *     }
 *     is Resource.Error -> {
 *         val error = result.exception
 *         // Show error message
 *     }
 *     is Resource.Loading -> {
 *         // Show loading indicator
 *     }
 * }
 * ```
 *
 * ## Why This Pattern:
 * - Provides compile-time safety for handling all result states
 * - Eliminates null pointer exceptions from unhandled states
 * - Makes error handling explicit and impossible to ignore
 * - Enables consistent error handling across the entire application
 *
 * @param T The type of data wrapped by this Resource
 */
sealed class Resource<out T> {
    
    /**
     * Represents a successful operation with the resulting data.
     *
     * @param data The successfully retrieved or computed data
     */
    data class Success<T>(val data: T) : Resource<T>()
    
    /**
     * Represents a failed operation with the exception that caused the failure.
     *
     * The exception can be used to determine the type of error (network, database, business logic)
     * and to extract appropriate error messages for display to the user.
     *
     * @param exception The throwable that caused the operation to fail
     * @param message Optional user-friendly error message
     */
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : Resource<Nothing>()
    
    /**
     * Represents an ongoing operation.
     *
     * This state is typically used to show loading indicators in the UI.
     * It contains no data as the operation has not yet completed.
     */
    data object Loading : Resource<Nothing>()
}

/**
 * Extension function to check if this Resource represents a successful result.
 *
 * @return true if this Resource is Success, false otherwise
 */
fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success

/**
 * Extension function to check if this Resource represents an error result.
 *
 * @return true if this Resource is Error, false otherwise
 */
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error

/**
 * Extension function to check if this Resource represents a loading state.
 *
 * @return true if this Resource is Loading, false otherwise
 */
fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading

/**
 * Extension function to get the data if this Resource is Success, or null otherwise.
 *
 * @return The data if Success, null otherwise
 */
fun <T> Resource<T>.getDataOrNull(): T? = when (this) {
    is Resource.Success -> data
    else -> null
}

/**
 * Extension function to get the exception if this Resource is Error, or null otherwise.
 *
 * @return The exception if Error, null otherwise
 */
fun <T> Resource<T>.getExceptionOrNull(): Throwable? = when (this) {
    is Resource.Error -> exception
    else -> null
}

/**
 * Extension function to transform the data in a Success Resource.
 *
 * This is useful for mapping data from one type to another while preserving
 * the Resource wrapper and error handling.
 *
 * @param transform The transformation function to apply to the data
 * @return A new Resource with the transformed data, or the original Error/Loading
 */
inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> Resource.Error(exception, message)
    is Resource.Loading -> Resource.Loading
}
