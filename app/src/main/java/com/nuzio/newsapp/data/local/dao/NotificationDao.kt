package com.nuzio.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nuzio.newsapp.data.local.entity.NotificationEntity

import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for notification operations
 */
@Dao
interface NotificationDao {

    /**
     * Insert a notification
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    /**
     * Insert multiple notifications
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    /**
     * Update a notification
     */
    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    /**
     * Get all notifications ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    /**
     * Get all notifications as list (for one-time fetch)
     */
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    suspend fun getAllNotificationsList(): List<NotificationEntity>

    /**
     * Get unread notifications
     */
    @Query("SELECT * FROM notifications WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>

    /**
     * Get notifications by type
     */
    @Query("SELECT * FROM notifications WHERE type = :type ORDER BY timestamp DESC")
    fun getNotificationsByType(type: String): Flow<List<NotificationEntity>>

    /**
     * Get notifications by section
     */
    @Query("SELECT * FROM notifications WHERE section = :section ORDER BY timestamp DESC")
    fun getNotificationsBySection(section: String): Flow<List<NotificationEntity>>

    /**
     * Get notification by ID
     */
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: String): NotificationEntity?

    /**
     * Mark notification as read
     */
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    /**
     * Mark all notifications as read
     */
    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    /**
     * Delete notification by ID
     */
    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    /**
     * Delete all notifications
     */
    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()

    /**
     * Delete notifications older than specified timestamp
     */
    @Query("DELETE FROM notifications WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    /**
     * Get count of unread notifications
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    /**
     * Get count of all notifications
     */
    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getNotificationCount(): Int
}
