package com.nuzio.newsapp.domain.usecase.notifications

import com.nuzio.newsapp.domain.model.NotificationPreference
import com.nuzio.newsapp.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * Use case for managing FCM topic subscriptions
 */
class SubscribeToTopicUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    /**
     * Subscribe to a specific topic
     */
    suspend fun subscribe(topic: String) {
        repository.subscribeToTopic(topic)
    }
    
    /**
     * Unsubscribe from a specific topic
     */
    suspend fun unsubscribe(topic: String) {
        repository.unsubscribeFromTopic(topic)
    }
    
    /**
     * Sync all topic subscriptions based on preferences
     */
    suspend fun syncSubscriptions(preferences: NotificationPreference) {
        repository.syncTopicSubscriptions(preferences)
    }
}
