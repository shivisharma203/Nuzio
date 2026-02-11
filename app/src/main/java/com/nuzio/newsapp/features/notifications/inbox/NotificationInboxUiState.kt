package com.nuzio.newsapp.features.notifications.inbox

import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.model.NotificationType


/**
 * UI State for Notification Inbox Screen
 */
data class NotificationInboxUiState(
    val notifications: List<NotificationData> = emptyList(),
    val filteredNotifications: List<NotificationData> = emptyList(),
    val unreadCount: Int = 0,
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val notificationToDelete: String? = null
) {
    /**
     * Check if there are any notifications
     */
    fun hasNotifications(): Boolean = notifications.isNotEmpty()

    /**
     * Check if filtered list is empty
     */
    fun isFilteredEmpty(): Boolean = filteredNotifications.isEmpty()

    /**
     * Get empty state message based on filter
     */
    fun getEmptyStateMessage(): String {
        return when (selectedFilter) {
            NotificationFilter.ALL -> "No notifications yet"
            NotificationFilter.UNREAD -> "No unread notifications"
            NotificationFilter.BREAKING_NEWS -> "No breaking news alerts"
            NotificationFilter.SECTION_SPECIFIC -> "No section notifications"
        }
    }
}

/**
 * Filter options for notifications
 */
enum class NotificationFilter(val displayName: String) {
    ALL("All"),
    UNREAD("Unread"),
    BREAKING_NEWS("Breaking News"),
    SECTION_SPECIFIC("Sections");

    companion object {
        fun fromNotificationType(type: NotificationType): NotificationFilter {
            return when (type) {
                NotificationType.BREAKING_NEWS -> BREAKING_NEWS
                NotificationType.SECTION_SPECIFIC -> SECTION_SPECIFIC
                else -> ALL
            }
        }
    }
}

/**
 * Events that can occur on the Notification Inbox Screen
 */
sealed class NotificationInboxEvent {
    object LoadNotifications : NotificationInboxEvent()
    object Refresh : NotificationInboxEvent()
    data class FilterChanged(val filter: NotificationFilter) : NotificationInboxEvent()
    data class NotificationClicked(val notification: NotificationData) : NotificationInboxEvent()
    data class MarkAsRead(val notificationId: String) : NotificationInboxEvent()
    object MarkAllAsRead : NotificationInboxEvent()
    data class DeleteNotification(val notificationId: String) : NotificationInboxEvent()
    data class ShowDeleteConfirmation(val notificationId: String) : NotificationInboxEvent()
    object DismissDeleteConfirmation : NotificationInboxEvent()
    object ConfirmDelete : NotificationInboxEvent()
    object ClearAllNotifications : NotificationInboxEvent()
    object ClearError : NotificationInboxEvent()
}
