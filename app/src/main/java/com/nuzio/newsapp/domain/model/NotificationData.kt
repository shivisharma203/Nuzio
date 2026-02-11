package com.nuzio.newsapp.domain.model

/**
 * Domain model representing a notification
 *
 * @property id Unique identifier for the notification
 * @property title Notification title
 * @property message Notification message/body
 * @property type Type of notification (breaking, section-specific, etc.)
 * @property articleUrl Optional deep link to the article
 * @property imageUrl Optional notification image URL
 * @property section Optional news section (for section-specific notifications)
 * @property timestamp When the notification was received
 * @property isRead Whether the notification has been read
 * @property data Additional custom data payload
 */
data class NotificationData(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val articleUrl: String? = null,
    val imageUrl: String? = null,
    val section: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val data: Map<String, String> = emptyMap()
) {
    /**
     * Mark this notification as read
     */
    fun markAsRead(): NotificationData {
        return copy(isRead = true)
    }

    /**
     * Check if notification has a deep link
     */
    fun hasDeepLink(): Boolean {
        return !articleUrl.isNullOrBlank()
    }

    /**
     * Check if notification has an image
     */
    fun hasImage(): Boolean {
        return !imageUrl.isNullOrBlank()
    }

    companion object {
        /**
         * Create NotificationData from FCM data payload
         */
        fun fromFcmData(
            messageId: String,
            data: Map<String, String>
        ): NotificationData {
            return NotificationData(
                id = messageId,
                title = data["title"] ?: "New Notification",
                message = data["message"] ?: data["body"] ?: "",
                type = NotificationType.fromString(data["type"]),
                articleUrl = data["article_url"],
                imageUrl = data["image_url"],
                section = data["section"],
                timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis(),
                isRead = false,
                data = data
            )
        }
    }
}
