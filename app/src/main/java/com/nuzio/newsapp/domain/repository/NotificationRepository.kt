package com.nuzio.newsapp.domain.repository

import com.nuzio.newsapp.data.local.entity.NotificationEntity
import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.model.NotificationPreference
import com.nuzio.newsapp.domain.model.NotificationType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for notification operations
 */
interface NotificationRepository {

    // ===== Notification CRUD =====

    /**
     * Save a notification to local storage
     */
    suspend fun saveNotification(notification: NotificationData)

    /**
     * Get all notifications as Flow
     */
    fun getAllNotifications(): Flow<List<NotificationData>>

    /**
     * Get unread notifications as Flow
     */
    fun getUnreadNotifications(): Flow<List<NotificationData>>

    /**
     * Get notifications by type
     */
    fun getNotificationsByType(type: NotificationType): Flow<List<NotificationData>>

    /**
     * Get notifications by section
     */
    fun getNotificationsBySection(section: String): Flow<List<NotificationData>>

    /**
     * Get notification by ID
     */
    suspend fun getNotificationById(id: String): NotificationData?

    /**
     * Mark notification as read
     */
    suspend fun markAsRead(id: String)

    /**
     * Mark all notifications as read
     */
    suspend fun markAllAsRead()

    /**
     * Delete notification
     */
    suspend fun deleteNotification(id: String)

    /**
     * Delete all notifications
     */
    suspend fun deleteAllNotifications()

    /**
     * Delete old notifications (older than specified days)
     */
    suspend fun deleteOldNotifications(olderThanDays: Int)

    /**
     * Get count of unread notifications
     */
    fun getUnreadCount(): Flow<Int>

    // ===== Preferences =====

    /**
     * Get notification preferences as Flow
     */
    fun getNotificationPreferences(): Flow<NotificationPreference>

    /**
     * Update notification preferences
     */
    suspend fun updateNotificationPreferences(preference: NotificationPreference)

    /**
     * Update master notifications toggle
     */
    suspend fun setNotificationsEnabled(enabled: Boolean)

    /**
     * Update breaking news toggle
     */
    suspend fun setBreakingNewsEnabled(enabled: Boolean)

    /**
     * Update section notifications toggle
     */
    suspend fun setSectionNotificationsEnabled(enabled: Boolean)

    /**
     * Toggle a specific section
     */
    suspend fun toggleSection(section: String)

    /**
     * Update sound preference
     */
    suspend fun setSoundEnabled(enabled: Boolean)

    /**
     * Update vibration preference
     */
    suspend fun setVibrationEnabled(enabled: Boolean)

    // ===== FCM Token Management =====

    /**
     * Get current FCM token
     */
    suspend fun getFcmToken(): String?

    /**
     * Save FCM token
     */
    suspend fun saveFcmToken(token: String)

    /**
     * Subscribe to FCM topic
     */
    suspend fun subscribeToTopic(topic: String)

    /**
     * Unsubscribe from FCM topic
     */
    suspend fun unsubscribeFromTopic(topic: String)

    /**
     * Subscribe to all enabled topics based on preferences
     */
    suspend fun syncTopicSubscriptions(preferences: NotificationPreference)
}
