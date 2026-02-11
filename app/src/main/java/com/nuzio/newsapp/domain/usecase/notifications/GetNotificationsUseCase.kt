package com.nuzio.newsapp.domain.usecase.notifications

import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving notifications
 */
class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    /**
     * Get all notifications
     */
    fun getAllNotifications(): Flow<List<NotificationData>> {
        return repository.getAllNotifications()
    }
    
    /**
     * Get unread notifications
     */
    fun getUnreadNotifications(): Flow<List<NotificationData>> {
        return repository.getUnreadNotifications()
    }
    
    /**
     * Get unread count
     */
    fun getUnreadCount(): Flow<Int> {
        return repository.getUnreadCount()
    }
}
