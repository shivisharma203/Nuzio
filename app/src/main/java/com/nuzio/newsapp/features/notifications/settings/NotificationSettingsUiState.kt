package com.nuzio.newsapp.features.settings

import com.nuzio.newsapp.domain.model.NotificationPreference

/**
 * UI State for Notification Settings Screen
 */
data class NotificationSettingsUiState(
    val preferences: NotificationPreference = NotificationPreference.DEFAULT,
    val isLoading: Boolean = false,
    val error: String? = null,
    val availableSections: List<String> = listOf(
        "TOP_STORIES",
        "WORLD",
        "POLITICS",
        "BUSINESS",
        "TECHNOLOGY",
        "HEALTH",
        "ENTERTAINMENT",
        "SPORTS",
        "SCIENCE"
    )
) {
    /**
     * Check if a section is enabled
     */
    fun isSectionEnabled(section: String): Boolean {
        return preferences.isSectionEnabled(section)
    }
    
    /**
     * Get formatted section name for display
     */
    fun getSectionDisplayName(section: String): String {
        return section.lowercase()
            .split("_")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
    }
}

/**
 * Events that can occur on the Notification Settings Screen
 */
sealed class NotificationSettingsEvent {
    data class ToggleMasterSwitch(val enabled: Boolean) : NotificationSettingsEvent()
    data class ToggleBreakingNews(val enabled: Boolean) : NotificationSettingsEvent()
    data class ToggleSectionNotifications(val enabled: Boolean) : NotificationSettingsEvent()
    data class ToggleSection(val section: String) : NotificationSettingsEvent()
    data class ToggleSound(val enabled: Boolean) : NotificationSettingsEvent()
    data class ToggleVibration(val enabled: Boolean) : NotificationSettingsEvent()
    object ClearError : NotificationSettingsEvent()
}
