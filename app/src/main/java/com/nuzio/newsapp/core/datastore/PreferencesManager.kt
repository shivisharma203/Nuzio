package com.nuzio.newsapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nuzio_preferences")


/**
 * Data source managing application preferences through DataStore.
 *
 * Provides type-safe access to user preferences including theme settings,
 * notification preferences, news category selections, and onboarding state.
 * All operations execute asynchronously through coroutines and expose data
 * through Flow for reactive observation of preference changes.
 */
@Singleton
class PreferencesDataSource @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_SELECTED_COUNTRY = stringPreferencesKey("selected_country")
        private val KEY_SELECTED_CATEGORY = stringPreferencesKey("selected_category")
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_OFFLINE_MODE_ENABLED = booleanPreferencesKey("offline_mode_enabled")
    }

    /**
     * Observes the selected theme mode as a Flow.
     * Returns "system" by default for automatic theme matching.
     */
    val themeMode: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[KEY_THEME_MODE] ?: "system"
        }

    /**
     * Observes notification enabled state as a Flow.
     * Returns true by default to enable notifications on first launch.
     */
    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
        }

    /**
     * Observes selected news country as a Flow.
     * Returns "us" by default for United States news.
     */
    val selectedCountry: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[KEY_SELECTED_COUNTRY] ?: "us"
        }

    /**
     * Observes selected news category as a Flow.
     * Returns null by default to show all categories.
     */
    val selectedCategory: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[KEY_SELECTED_CATEGORY]
        }

    /**
     * Observes onboarding completion state as a Flow.
     * Returns false by default to show onboarding on first launch.
     */
    val onboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] ?: false
        }

    /**
     * Observes offline mode enabled state as a Flow.
     * Returns false by default to prefer online content.
     */
    val offlineModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_OFFLINE_MODE_ENABLED] ?: false
        }

    /**
     * Updates the theme mode preference.
     *
     * @param mode Theme mode: "light", "dark", or "system"
     */
    suspend fun setThemeMode(mode: String) {
        try {
            dataStore.edit { preferences ->
                preferences[KEY_THEME_MODE] = mode
            }
            Timber.d("💾 Theme mode updated: $mode")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update theme mode")
            throw e
        }
    }

    /**
     * Updates the notifications enabled preference.
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
            }
            Timber.d("💾 Notifications ${if (enabled) "enabled" else "disabled"}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update notifications setting")
            throw e
        }
    }

    /**
     * Updates the selected news country preference.
     */
    suspend fun setSelectedCountry(country: String) {
        try {
            dataStore.edit { preferences ->
                preferences[KEY_SELECTED_COUNTRY] = country
            }
            Timber.d("💾 Selected country updated: $country")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update selected country")
            throw e
        }
    }

    /**
     * Updates the selected news category preference.
     */
    suspend fun setSelectedCategory(category: String?) {
        try {
            dataStore.edit { preferences ->
                if (category != null) {
                    preferences[KEY_SELECTED_CATEGORY] = category
                } else {
                    preferences.remove(KEY_SELECTED_CATEGORY)
                }
            }
            Timber.d("💾 Selected category updated: ${category ?: "all"}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update selected category")
            throw e
        }
    }

    /**
     * Marks onboarding as completed.
     */
    suspend fun setOnboardingCompleted() {
        try {
            dataStore.edit { preferences ->
                preferences[KEY_ONBOARDING_COMPLETED] = true
            }
            Timber.d("💾 Onboarding marked as completed")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update onboarding status")
            throw e
        }
    }

    /**
     * Updates the offline mode enabled preference.
     */
    suspend fun setOfflineModeEnabled(enabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[KEY_OFFLINE_MODE_ENABLED] = enabled
            }
            Timber.d("💾 Offline mode ${if (enabled) "enabled" else "disabled"}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to update offline mode setting")
            throw e
        }
    }

    /**
     * Clears all preferences, returning to default values.
     * Useful for logout operations or factory reset scenarios.
     */
    suspend fun clearAllPreferences() {
        try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
            Timber.d("💾 All preferences cleared")
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear preferences")
            throw e
        }
    }
}