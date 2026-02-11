package com.nuzio.newsapp.domain.usecase.notifications

import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * Use case for saving a notification to local storage
 */
class SaveNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notification: NotificationData) {
        repository.saveNotification(notification)
    }
}
