package com.nuzio.newsapp.data.repository

import com.nuzio.newsapp.data.local.PreferencesDataSource
import com.nuzio.newsapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PreferencesRepository delegating to PreferencesDataSource.
 *
 * This implementation maintains clean architecture by providing the domain
 * layer interface while delegating actual persistence operations to the
 * data source abstraction in the data layer.
 */
@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : PreferencesRepository {

    override val themeMode: Flow<String> = preferencesDataSource.themeMode

    override val notificationsEnabled: Flow<Boolean> = preferencesDataSource.notificationsEnabled

    override val selectedCountry: Flow<String> = preferencesDataSource.selectedCountry

    override val selectedCategory: Flow<String?> = preferencesDataSource.selectedCategory

    override val onboardingCompleted: Flow<Boolean> = preferencesDataSource.onboardingCompleted

    override val offlineModeEnabled: Flow<Boolean> = preferencesDataSource.offlineModeEnabled

    override suspend fun setThemeMode(mode: String) {
        preferencesDataSource.setThemeMode(mode)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        preferencesDataSource.setNotificationsEnabled(enabled)
    }

    override suspend fun setSelectedCountry(country: String) {
        preferencesDataSource.setSelectedCountry(country)
    }

    override suspend fun setSelectedCategory(category: String?) {
        preferencesDataSource.setSelectedCategory(category)
    }

    override suspend fun setOnboardingCompleted() {
        preferencesDataSource.setOnboardingCompleted()
    }

    override suspend fun setOfflineModeEnabled(enabled: Boolean) {
        preferencesDataSource.setOfflineModeEnabled(enabled)
    }

    override suspend fun clearAllPreferences() {
        preferencesDataSource.clearAllPreferences()
    }
}