package com.nuzio.newsapp.domain.usecase.notifications

import com.nuzio.newsapp.domain.model.NotificationPreference
import com.nuzio.newsapp.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * Use case for updating notification preferences
 */
class UpdateNotificationPreferencesUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(preference: NotificationPreference) {
        repository.updateNotificationPreferences(preference)
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        repository.setNotificationsEnabled(enabled)
    }
    
    suspend fun setBreakingNewsEnabled(enabled: Boolean) {
        repository.setBreakingNewsEnabled(enabled)
    }
    
    suspend fun setSectionNotificationsEnabled(enabled: Boolean) {
        repository.setSectionNotificationsEnabled(enabled)
    }
    
    suspend fun toggleSection(section: String) {
        repository.toggleSection(section)
    }
    
    suspend fun setSoundEnabled(enabled: Boolean) {
        repository.setSoundEnabled(enabled)
    }
    
    suspend fun setVibrationEnabled(enabled: Boolean) {
        repository.setVibrationEnabled(enabled)
    }
}
