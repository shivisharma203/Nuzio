package com.nuzio.newsapp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing application preferences.
 *
 * Defines the contract for preference access without exposing
 * implementation details about the underlying storage mechanism.
 * This abstraction enables dependency inversion where high-level
 * business logic depends on interfaces rather than concrete implementations.
 */
interface PreferencesRepository {

    val themeMode: Flow<String>
    val notificationsEnabled: Flow<Boolean>
    val selectedCountry: Flow<String>
    val selectedCategory: Flow<String?>
    val onboardingCompleted: Flow<Boolean>
    val offlineModeEnabled: Flow<Boolean>

    suspend fun setThemeMode(mode: String)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setSelectedCountry(country: String)
    suspend fun setSelectedCategory(category: String?)
    suspend fun setOnboardingCompleted()
    suspend fun setOfflineModeEnabled(enabled: Boolean)
    suspend fun clearAllPreferences()
}