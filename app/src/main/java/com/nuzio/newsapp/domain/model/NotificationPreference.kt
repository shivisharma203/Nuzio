package com.nuzio.newsapp.domain.model

/**
 * Domain model representing user's notification preferences
 * 
 * @property notificationsEnabled Master toggle for all notifications
 * @property breakingNewsEnabled Enable breaking news alerts
 * @property sectionNotificationsEnabled Enable section-specific notifications
 * @property enabledSections Set of enabled news sections for notifications
 * @property soundEnabled Play notification sound
 * @property vibrationEnabled Vibrate on notification
 */
data class NotificationPreference(
    val notificationsEnabled: Boolean = true,
    val breakingNewsEnabled: Boolean = true,
    val sectionNotificationsEnabled: Boolean = false,
    val enabledSections: Set<String> = emptySet(),
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) {
    /**
     * Check if a specific section is enabled
     */
    fun isSectionEnabled(section: String): Boolean {
        return sectionNotificationsEnabled && enabledSections.contains(section)
    }
    
    /**
     * Check if any notifications are enabled
     */
    fun hasAnyNotificationsEnabled(): Boolean {
        return notificationsEnabled && (breakingNewsEnabled || sectionNotificationsEnabled)
    }
    
    /**
     * Toggle a section on/off
     */
    fun toggleSection(section: String): NotificationPreference {
        val newSections = if (enabledSections.contains(section)) {
            enabledSections - section
        } else {
            enabledSections + section
        }
        return copy(enabledSections = newSections)
    }
    
    /**
     * Enable all provided sections
     */
    fun enableSections(sections: Set<String>): NotificationPreference {
        return copy(enabledSections = enabledSections + sections)
    }
    
    /**
     * Disable all provided sections
     */
    fun disableSections(sections: Set<String>): NotificationPreference {
        return copy(enabledSections = enabledSections - sections)
    }
    
    companion object {
        /**
         * Default notification preferences
         */
        val DEFAULT = NotificationPreference()
    }
}
