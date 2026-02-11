package com.nuzio.newsapp.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuzio.newsapp.domain.usecase.notifications.GetNotificationPreferencesUseCase
import com.nuzio.newsapp.domain.usecase.notifications.SubscribeToTopicUseCase
import com.nuzio.newsapp.domain.usecase.notifications.UpdateNotificationPreferencesUseCase
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
 * ViewModel for Notification Settings Screen
 */
@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val getNotificationPreferencesUseCase: GetNotificationPreferencesUseCase,
    private val updateNotificationPreferencesUseCase: UpdateNotificationPreferencesUseCase,
    private val subscribeToTopicUseCase: SubscribeToTopicUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadPreferences()
    }
    
    /**
     * Load current notification preferences
     */
    private fun loadPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getNotificationPreferencesUseCase()
                .catch { error ->
                    Timber.e(error, "Error loading notification preferences")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load preferences: ${error.message}"
                        )
                    }
                }
                .collect { preferences ->
                    _uiState.update { 
                        it.copy(
                            preferences = preferences,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }
    
    /**
     * Handle UI events
     */
    fun onEvent(event: NotificationSettingsEvent) {
        when (event) {
            is NotificationSettingsEvent.ToggleMasterSwitch -> handleMasterToggle(event.enabled)
            is NotificationSettingsEvent.ToggleBreakingNews -> handleBreakingNewsToggle(event.enabled)
            is NotificationSettingsEvent.ToggleSectionNotifications -> handleSectionNotificationsToggle(event.enabled)
            is NotificationSettingsEvent.ToggleSection -> handleSectionToggle(event.section)
            is NotificationSettingsEvent.ToggleSound -> handleSoundToggle(event.enabled)
            is NotificationSettingsEvent.ToggleVibration -> handleVibrationToggle(event.enabled)
            NotificationSettingsEvent.ClearError -> clearError()
            else -> {}
        }
    }
    
    /**
     * Handle master notifications toggle
     */
    private fun handleMasterToggle(enabled: Boolean) {
        viewModelScope.launch {
            try {
                updateNotificationPreferencesUseCase.setNotificationsEnabled(enabled)
                
                // Sync topic subscriptions
                val currentPrefs = _uiState.value.preferences
                subscribeToTopicUseCase.syncSubscriptions(
                    currentPrefs.copy(notificationsEnabled = enabled)
                )
            } catch (e: Exception) {
                Timber.e(e, "Error toggling master notifications")
                _uiState.update { it.copy(error = "Failed to update: ${e.message}") }
            }
        }
    }
    
    /**
     * Handle breaking news toggle
     */
    private fun handleBreakingNewsToggle(enabled: Boolean) {
        viewModelScope.launch {
            try {
                updateNotificationPreferencesUseCase.setBreakingNewsEnabled(enabled)
            } catch (e: Exception) {
                Timber.e(e, "Error toggling breaking news")
                _uiState.update { it.copy(error = "Failed to update: ${e.message}") }
            }
        }
    }
    
    /**
     * Handle section notifications toggle
     */
    private fun handleSectionNotificationsToggle(enabled: Boolean) {
        viewModelScope.launch {
            try {
                updateNotificationPreferencesUseCase.setSectionNotificationsEnabled(enabled)
                
                // If enabling, sync subscriptions for enabled sections
                if (enabled) {
                    val currentPrefs = _uiState.value.preferences
                    subscribeToTopicUseCase.syncSubscriptions(
                        currentPrefs.copy(sectionNotificationsEnabled = enabled)
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling section notifications")
                _uiState.update { it.copy(error = "Failed to update: ${e.message}") }
            }
        }
    }
    
    /**
     * Handle individual section toggle
     */
    private fun handleSectionToggle(section: String) {
        viewModelScope.launch {
            try {
                updateNotificationPreferencesUseCase.toggleSection(section)
            } catch (e: Exception) {
                Timber.e(e, "Error toggling section: $section")
                _uiState.update { it.copy(error = "Failed to update: ${e.message}") }
            }
        }
    }
    
    /**
     * Handle sound toggle
     */
    private fun handleSoundToggle(enabled: Boolean) {
        viewModelScope.launch {
            try {
                updateNotificationPreferencesUseCase.setSoundEnabled(enabled)
            } catch (e: Exception) {
                Timber.e(e, "Error toggling sound")
                _uiState.update { it.copy(error = "Failed to update: ${e.message}") }
            }
        }
    }
    
    /**
     * Handle vibration toggle
     */
    private fun handleVibrationToggle(enabled: Boolean) {
        viewModelScope.launch {
            try {
                updateNotificationPreferencesUseCase.setVibrationEnabled(enabled)
            } catch (e: Exception) {
                Timber.e(e, "Error toggling vibration")
                _uiState.update { it.copy(error = "Failed to update: ${e.message}") }
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
