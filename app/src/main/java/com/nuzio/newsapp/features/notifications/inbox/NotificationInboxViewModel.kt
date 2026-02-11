package com.nuzio.newsapp.features.notifications.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.model.NotificationType
import com.nuzio.newsapp.domain.repository.NotificationRepository
import com.nuzio.newsapp.domain.usecase.notifications.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * ViewModel for Notification Inbox Screen
 */
@HiltViewModel
class NotificationInboxViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationInboxUiState())
    val uiState: StateFlow<NotificationInboxUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        loadUnreadCount()
    }

    /**
     * Handle UI events
     */
    fun onEvent(event: NotificationInboxEvent) {
        when (event) {
            is NotificationInboxEvent.LoadNotifications -> loadNotifications()
            is NotificationInboxEvent.Refresh -> refreshNotifications()
            is NotificationInboxEvent.FilterChanged -> applyFilter(event.filter)
            is NotificationInboxEvent.NotificationClicked -> handleNotificationClick(event.notification)
            is NotificationInboxEvent.MarkAsRead -> markAsRead(event.notificationId)
            is NotificationInboxEvent.MarkAllAsRead -> markAllAsRead()
            is NotificationInboxEvent.DeleteNotification -> deleteNotification(event.notificationId)
            is NotificationInboxEvent.ShowDeleteConfirmation -> showDeleteConfirmation(event.notificationId)
            is NotificationInboxEvent.DismissDeleteConfirmation -> dismissDeleteConfirmation()
            is NotificationInboxEvent.ConfirmDelete -> confirmDelete()
            is NotificationInboxEvent.ClearAllNotifications -> clearAllNotifications()
            is NotificationInboxEvent.ClearError -> clearError()
        }
    }

    /**
     * Load all notifications
     */
    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getNotificationsUseCase.getAllNotifications()
                .catch { error ->
                    Timber.e(error, "Error loading notifications")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load notifications: ${error.message}"
                        )
                    }
                }
                .collect { notifications ->
                    _uiState.update { state ->
                        state.copy(
                            notifications = notifications,
                            filteredNotifications = filterNotifications(notifications, state.selectedFilter),
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Load unread count
     */
    private fun loadUnreadCount() {
        viewModelScope.launch {
            getNotificationsUseCase.getUnreadCount()
                .catch { error ->
                    Timber.e(error, "Error loading unread count")
                }
                .collect { count ->
                    _uiState.update { it.copy(unreadCount = count) }
                }
        }
    }

    /**
     * Refresh notifications with pull-to-refresh
     */
    private fun refreshNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            // In a real app, you might fetch from server here
            // For now, we just reload from local database

            try {
                // Simulate network delay
                kotlinx.coroutines.delay(500)

                _uiState.update { it.copy(isRefreshing = false) }
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing notifications")
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = "Failed to refresh: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Apply filter to notifications
     */
    private fun applyFilter(filter: NotificationFilter) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredNotifications = filterNotifications(state.notifications, filter)
            )
        }
    }

    /**
     * Filter notifications based on selected filter
     */
    private fun filterNotifications(
        notifications: List<NotificationData>,
        filter: NotificationFilter
    ): List<NotificationData> {
        return when (filter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.BREAKING_NEWS -> notifications.filter {
                it.type == NotificationType.BREAKING_NEWS
            }
            NotificationFilter.SECTION_SPECIFIC -> notifications.filter {
                it.type == NotificationType.SECTION_SPECIFIC
            }
        }
    }

    /**
     * Handle notification click - mark as read and navigate
     */
    private fun handleNotificationClick(notification: NotificationData) {
        // Mark as read if unread
        if (!notification.isRead) {
            markAsRead(notification.id)
        }
        // Navigation is handled by the UI layer
    }

    /**
     * Mark single notification as read
     */
    private fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notificationId)
                Timber.d("Marked notification as read: $notificationId")
            } catch (e: Exception) {
                Timber.e(e, "Error marking notification as read")
                _uiState.update {
                    it.copy(error = "Failed to mark as read: ${e.message}")
                }
            }
        }
    }

    /**
     * Mark all notifications as read
     */
    private fun markAllAsRead() {
        viewModelScope.launch {
            try {
                notificationRepository.markAllAsRead()
                Timber.d("Marked all notifications as read")
            } catch (e: Exception) {
                Timber.e(e, "Error marking all as read")
                _uiState.update {
                    it.copy(error = "Failed to mark all as read: ${e.message}")
                }
            }
        }
    }

    /**
     * Delete single notification
     */
    private fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.deleteNotification(notificationId)
                Timber.d("Deleted notification: $notificationId")
            } catch (e: Exception) {
                Timber.e(e, "Error deleting notification")
                _uiState.update {
                    it.copy(error = "Failed to delete notification: ${e.message}")
                }
            }
        }
    }

    /**
     * Show delete confirmation dialog
     */
    private fun showDeleteConfirmation(notificationId: String) {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = true,
                notificationToDelete = notificationId
            )
        }
    }

    /**
     * Dismiss delete confirmation dialog
     */
    private fun dismissDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                notificationToDelete = null
            )
        }
    }

    /**
     * Confirm and execute delete
     */
    private fun confirmDelete() {
        val notificationId = _uiState.value.notificationToDelete
        if (notificationId != null) {
            deleteNotification(notificationId)
        }
        dismissDeleteConfirmation()
    }

    /**
     * Clear all notifications
     */
    private fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                notificationRepository.deleteAllNotifications()
                Timber.d("Cleared all notifications")
            } catch (e: Exception) {
                Timber.e(e, "Error clearing notifications")
                _uiState.update {
                    it.copy(error = "Failed to clear notifications: ${e.message}")
                }
            }
        }
    }

    /**
     * Clear error message
     */
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
