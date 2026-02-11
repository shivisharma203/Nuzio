package com.nuzio.newsapp.domain.usecase.notifications

import com.nuzio.newsapp.domain.model.NotificationPreference
import com.nuzio.newsapp.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving notification preferences
 */
class GetNotificationPreferencesUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<NotificationPreference> {
        return repository.getNotificationPreferences()
    }
}
