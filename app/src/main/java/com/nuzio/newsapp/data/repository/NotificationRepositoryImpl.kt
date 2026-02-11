package com.nuzio.newsapp.data.repository


import com.nuzio.newsapp.core.datastore.NotificationPreferencesDataStore
import com.nuzio.newsapp.data.local.dao.NotificationDao
import com.nuzio.newsapp.data.local.entity.toDomain
import com.nuzio.newsapp.data.local.entity.toEntity
import com.nuzio.newsapp.data.remote.FcmTokenManager
import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.model.NotificationPreference
import com.nuzio.newsapp.domain.model.NotificationType
import com.nuzio.newsapp.domain.model.NotificationType.Companion.getSectionTopic
import com.nuzio.newsapp.domain.model.NotificationType.Companion.toTopicName
import com.nuzio.newsapp.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationRepository
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val preferencesDataStore: NotificationPreferencesDataStore,
    private val fcmTokenManager: FcmTokenManager
) : NotificationRepository {
    
    // ===== Notification CRUD =====
    
    override suspend fun saveNotification(notification: NotificationData) {
        notificationDao.insertNotification(notification.toEntity())
    }
    
    override fun getAllNotifications(): Flow<List<NotificationData>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.toDomain()
        }
    }
    
    override fun getUnreadNotifications(): Flow<List<NotificationData>> {
        return notificationDao.getUnreadNotifications().map { entities ->
            entities.toDomain()
        }
    }
    
    override fun getNotificationsByType(type: NotificationType): Flow<List<NotificationData>> {
        return notificationDao.getNotificationsByType(type.name).map { entities ->
            entities.toDomain()
        }
    }
    
    override fun getNotificationsBySection(section: String): Flow<List<NotificationData>> {
        return notificationDao.getNotificationsBySection(section).map { entities ->
            entities.toDomain()
        }
    }
    
    override suspend fun getNotificationById(id: String): NotificationData? {
        return notificationDao.getNotificationById(id)?.toDomain()
    }
    
    override suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }
    
    override suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }
    
    override suspend fun deleteNotification(id: String) {
        notificationDao.deleteNotification(id)
    }
    
    override suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }
    
    override suspend fun deleteOldNotifications(olderThanDays: Int) {
        val cutoffTimestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(olderThanDays.toLong())
        notificationDao.deleteOlderThan(cutoffTimestamp)
    }
    
    override fun getUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCount()
    }
    
    // ===== Preferences =====
    
    override fun getNotificationPreferences(): Flow<NotificationPreference> {
        return preferencesDataStore.notificationPreferences
    }
    
    override suspend fun updateNotificationPreferences(preference: NotificationPreference) {
        preferencesDataStore.updatePreferences(preference)
        // Sync topic subscriptions based on new preferences
        syncTopicSubscriptions(preference)
    }
    
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        preferencesDataStore.setNotificationsEnabled(enabled)
    }
    
    override suspend fun setBreakingNewsEnabled(enabled: Boolean) {
        preferencesDataStore.setBreakingNewsEnabled(enabled)
        
        // Subscribe/unsubscribe to breaking news topic
        val topic = NotificationType.BREAKING_NEWS.toTopicName()
        if (enabled) {
            fcmTokenManager.subscribeToTopic(topic)
        } else {
            fcmTokenManager.unsubscribeFromTopic(topic)
        }
    }
    
    override suspend fun setSectionNotificationsEnabled(enabled: Boolean) {
        preferencesDataStore.setSectionNotificationsEnabled(enabled)
    }
    
    override suspend fun toggleSection(section: String) {
        preferencesDataStore.toggleSection(section)
        
        // Get current preferences to determine if we should subscribe or unsubscribe
        var currentPreferences: NotificationPreference? = null
        preferencesDataStore.notificationPreferences.collect { prefs ->
            currentPreferences = prefs
            return@collect
        }
        
        currentPreferences?.let { prefs ->
            val topic = getSectionTopic(section)
            if (prefs.isSectionEnabled(section)) {
                fcmTokenManager.subscribeToTopic(topic)
            } else {
                fcmTokenManager.unsubscribeFromTopic(topic)
            }
        }
    }
    
    override suspend fun setSoundEnabled(enabled: Boolean) {
        preferencesDataStore.setSoundEnabled(enabled)
    }
    
    override suspend fun setVibrationEnabled(enabled: Boolean) {
        preferencesDataStore.setVibrationEnabled(enabled)
    }
    
    // ===== FCM Token Management =====
    
    override suspend fun getFcmToken(): String? {
        return fcmTokenManager.getToken()
    }
    
    override suspend fun saveFcmToken(token: String) {
        Timber.d("FCM token saved: $token")
        // In production, you might want to send this to your backend
        // For now, we just log it
    }
    
    override suspend fun subscribeToTopic(topic: String) {
        fcmTokenManager.subscribeToTopic(topic)
    }
    
    override suspend fun unsubscribeFromTopic(topic: String) {
        fcmTokenManager.unsubscribeFromTopic(topic)
    }
    
    override suspend fun syncTopicSubscriptions(preferences: NotificationPreference) {
        Timber.d("Syncing topic subscriptions based on preferences")
        
        if (!preferences.notificationsEnabled) {
            // Unsubscribe from all topics if notifications are disabled
            fcmTokenManager.unsubscribeFromTopic(NotificationType.BREAKING_NEWS.toTopicName())
            fcmTokenManager.unsubscribeFromTopic(NotificationType.GENERAL.toTopicName())
            return
        }
        
        // Breaking news subscription
        if (preferences.breakingNewsEnabled) {
            fcmTokenManager.subscribeToTopic(NotificationType.BREAKING_NEWS.toTopicName())
        } else {
            fcmTokenManager.unsubscribeFromTopic(NotificationType.BREAKING_NEWS.toTopicName())
        }
        
        // Section-specific subscriptions
        if (preferences.sectionNotificationsEnabled) {
            preferences.enabledSections.forEach { section ->
                fcmTokenManager.subscribeToTopic(getSectionTopic(section))
            }
        }
        
        Timber.d("Topic subscriptions synced successfully")
    }

}
