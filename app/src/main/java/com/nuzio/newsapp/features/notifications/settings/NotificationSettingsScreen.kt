package com.nuzio.newsapp.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Notification Settings Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NotificationSettingsContent(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
            
            // Show error snackbar
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(NotificationSettingsEvent.ClearError) }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsContent(
    uiState: NotificationSettingsUiState,
    onEvent: (NotificationSettingsEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // Master Toggle
        item {
            SettingsSection(title = "General")
        }
        
        item {
            SettingsSwitchItem(
                title = "Enable Notifications",
                description = "Receive news notifications from Nuzio",
                icon = Icons.Default.Notifications,
                checked = uiState.preferences.notificationsEnabled,
                onCheckedChange = { onEvent(NotificationSettingsEvent.ToggleMasterSwitch(it)) }
            )
        }
        
        // Breaking News Section
        item {
            SettingsSection(title = "Breaking News")
        }
        
        item {
            SettingsSwitchItem(
                title = "Breaking News Alerts",
                description = "Get notified about urgent breaking news",
                icon = Icons.Default.Campaign,
                checked = uiState.preferences.breakingNewsEnabled,
                onCheckedChange = { onEvent(NotificationSettingsEvent.ToggleBreakingNews(it)) },
                enabled = uiState.preferences.notificationsEnabled
            )
        }
        
        // Section Notifications
        item {
            SettingsSection(title = "Section Notifications")
        }
        
        item {
            SettingsSwitchItem(
                title = "Section-Specific Notifications",
                description = "Get notifications for your favorite news sections",
                icon = Icons.Default.Category,
                checked = uiState.preferences.sectionNotificationsEnabled,
                onCheckedChange = { onEvent(NotificationSettingsEvent.ToggleSectionNotifications(it)) },
                enabled = uiState.preferences.notificationsEnabled
            )
        }
        
        // Individual Sections
        if (uiState.preferences.sectionNotificationsEnabled && uiState.preferences.notificationsEnabled) {
            items(uiState.availableSections) { section ->
                SectionToggleItem(
                    sectionName = uiState.getSectionDisplayName(section),
                    checked = uiState.isSectionEnabled(section),
                    onCheckedChange = { onEvent(NotificationSettingsEvent.ToggleSection(section)) }
                )
            }
        }
        
        // Notification Behavior
        item {
            SettingsSection(title = "Notification Behavior")
        }
        
        item {
            SettingsSwitchItem(
                title = "Sound",
                description = "Play sound for notifications",
                icon = Icons.Default.VolumeUp,
                checked = uiState.preferences.soundEnabled,
                onCheckedChange = { onEvent(NotificationSettingsEvent.ToggleSound(it)) },
                enabled = uiState.preferences.notificationsEnabled
            )
        }
        
        item {
            SettingsSwitchItem(
                title = "Vibration",
                description = "Vibrate for notifications",
                icon = Icons.Default.Vibration,
                checked = uiState.preferences.vibrationEnabled,
                onCheckedChange = { onEvent(NotificationSettingsEvent.ToggleVibration(it)) },
                enabled = uiState.preferences.notificationsEnabled
            )
        }
        
        // Info section
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "You can manage notification permissions in your device settings. " +
                                "Some notification features may require specific permissions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.onSurface 
                      else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface 
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant 
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    }
    Divider(modifier = Modifier.padding(start = 56.dp))
}

@Composable
private fun SectionToggleItem(
    sectionName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sectionName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
