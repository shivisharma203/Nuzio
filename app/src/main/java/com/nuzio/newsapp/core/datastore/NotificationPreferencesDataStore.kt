package com.nuzio.newsapp.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nuzio.newsapp.domain.model.NotificationPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore extension for notification preferences
 */
private val Context.notificationPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "notification_preferences"
)

/**
 * DataStore implementation for notification preferences
 */
@Singleton
class NotificationPreferencesDataStore @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.notificationPreferencesDataStore
    
    companion object {
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_BREAKING_NEWS_ENABLED = booleanPreferencesKey("breaking_news_enabled")
        private val KEY_SECTION_NOTIFICATIONS_ENABLED = booleanPreferencesKey("section_notifications_enabled")
        private val KEY_ENABLED_SECTIONS = stringSetPreferencesKey("enabled_sections")
        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }
    
    /**
     * Get notification preferences as Flow
     */
    val notificationPreferences: Flow<NotificationPreference> = dataStore.data.map { preferences ->
        NotificationPreference(
            notificationsEnabled = preferences[KEY_NOTIFICATIONS_ENABLED] ?: true,
            breakingNewsEnabled = preferences[KEY_BREAKING_NEWS_ENABLED] ?: true,
            sectionNotificationsEnabled = preferences[KEY_SECTION_NOTIFICATIONS_ENABLED] ?: false,
            enabledSections = preferences[KEY_ENABLED_SECTIONS] ?: emptySet(),
            soundEnabled = preferences[KEY_SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[KEY_VIBRATION_ENABLED] ?: true
        )
    }
    
    /**
     * Update master notifications toggle
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    /**
     * Update breaking news toggle
     */
    suspend fun setBreakingNewsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_BREAKING_NEWS_ENABLED] = enabled
        }
    }
    
    /**
     * Update section notifications toggle
     */
    suspend fun setSectionNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SECTION_NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    /**
     * Update enabled sections
     */
    suspend fun setEnabledSections(sections: Set<String>) {
        dataStore.edit { preferences ->
            preferences[KEY_ENABLED_SECTIONS] = sections
        }
    }
    
    /**
     * Toggle a specific section
     */
    suspend fun toggleSection(section: String) {
        dataStore.edit { preferences ->
            val currentSections = preferences[KEY_ENABLED_SECTIONS] ?: emptySet()
            val newSections = if (currentSections.contains(section)) {
                currentSections - section
            } else {
                currentSections + section
            }
            preferences[KEY_ENABLED_SECTIONS] = newSections
        }
    }
    
    /**
     * Update sound preference
     */
    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SOUND_ENABLED] = enabled
        }
    }
    
    /**
     * Update vibration preference
     */
    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_VIBRATION_ENABLED] = enabled
        }
    }
    
    /**
     * Update all preferences at once
     */
    suspend fun updatePreferences(preference: NotificationPreference) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = preference.notificationsEnabled
            preferences[KEY_BREAKING_NEWS_ENABLED] = preference.breakingNewsEnabled
            preferences[KEY_SECTION_NOTIFICATIONS_ENABLED] = preference.sectionNotificationsEnabled
            preferences[KEY_ENABLED_SECTIONS] = preference.enabledSections
            preferences[KEY_SOUND_ENABLED] = preference.soundEnabled
            preferences[KEY_VIBRATION_ENABLED] = preference.vibrationEnabled
        }
    }
    
    /**
     * Clear all preferences (reset to defaults)
     */
    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
