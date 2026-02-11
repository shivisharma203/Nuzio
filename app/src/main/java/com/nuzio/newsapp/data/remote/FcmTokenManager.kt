package com.nuzio.newsapp.data.remote

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Manager for FCM token operations and topic subscriptions
 */
@Singleton
class FcmTokenManager @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) {
    
    /**
     * Get current FCM token
     */
    suspend fun getToken(): String? {
        return try {
            firebaseMessaging.token.await()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get FCM token")
            null
        }
    }
    
    /**
     * Delete current FCM token (for logout or token refresh)
     */
    suspend fun deleteToken() {
        try {
            firebaseMessaging.deleteToken().await()
            Timber.d("FCM token deleted successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete FCM token")
        }
    }
    
    /**
     * Subscribe to a topic
     */
    suspend fun subscribeToTopic(topic: String): Boolean {
        return suspendCoroutine { continuation ->
            firebaseMessaging.subscribeToTopic(topic)
                .addOnSuccessListener {
                    Timber.d("Successfully subscribed to topic: $topic")
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Timber.e(e, "Failed to subscribe to topic: $topic")
                    continuation.resume(false)
                }
        }
    }
    
    /**
     * Unsubscribe from a topic
     */
    suspend fun unsubscribeFromTopic(topic: String): Boolean {
        return suspendCoroutine { continuation ->
            firebaseMessaging.unsubscribeFromTopic(topic)
                .addOnSuccessListener {
                    Timber.d("Successfully unsubscribed from topic: $topic")
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Timber.e(e, "Failed to unsubscribe from topic: $topic")
                    continuation.resume(false)
                }
        }
    }
    
    /**
     * Subscribe to multiple topics
     */
    suspend fun subscribeToTopics(topics: List<String>): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        topics.forEach { topic ->
            results[topic] = subscribeToTopic(topic)
        }
        return results
    }
    
    /**
     * Unsubscribe from multiple topics
     */
    suspend fun unsubscribeFromTopics(topics: List<String>): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        topics.forEach { topic ->
            results[topic] = unsubscribeFromTopic(topic)
        }
        return results
    }
    
    /**
     * Check if FCM is available
     */
    fun isAvailable(): Boolean {
        return try {
            firebaseMessaging.isAutoInitEnabled
            true
        } catch (e: Exception) {
            Timber.e(e, "FCM is not available")
            false
        }
    }
    
    /**
     * Set auto-initialization enabled/disabled
     */
    fun setAutoInitEnabled(enabled: Boolean) {
        firebaseMessaging.isAutoInitEnabled = enabled
    }
}
