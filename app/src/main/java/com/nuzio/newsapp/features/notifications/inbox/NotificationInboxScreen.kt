package com.nuzio.newsapp.features.notifications.inbox


import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.features.notifications.components.SwipeableNotificationCard

/**
 * Notification Inbox Screen
 *
 * Displays all received notifications with filtering, mark as read, and delete capabilities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationInboxScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (NotificationData) -> Unit = {},
    viewModel: NotificationInboxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load notifications on first composition
    LaunchedEffect(Unit) {
        viewModel.onEvent(NotificationInboxEvent.LoadNotifications)
    }

    Scaffold(
        topBar = {
            NotificationInboxTopBar(
                unreadCount = uiState.unreadCount,
                onBackClick = onBackClick,
                onClearAll = {
                    viewModel.onEvent(NotificationInboxEvent.ClearAllNotifications)
                },
                hasNotifications = uiState.hasNotifications()
            )
        },
        floatingActionButton = {
            // Mark all as read FAB
            AnimatedVisibility(
                visible = uiState.unreadCount > 0,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onEvent(NotificationInboxEvent.MarkAllAsRead) },
                    icon = { Icon(Icons.Default.DoneAll, contentDescription = null) },
                    text = { Text("Mark All Read") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = uiState.isRefreshing),
                onRefresh = { viewModel.onEvent(NotificationInboxEvent.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Filter tabs
                    NotificationFilterTabs(
                        selectedFilter = uiState.selectedFilter,
                        unreadCount = uiState.unreadCount,
                        onFilterSelected = { filter ->
                            viewModel.onEvent(NotificationInboxEvent.FilterChanged(filter))
                        }
                    )

                    // Notification list or empty state
                    when {
                        uiState.isLoading -> LoadingView()
                        uiState.isFilteredEmpty() -> EmptyStateView(
                            message = uiState.getEmptyStateMessage()
                        )
                        else -> NotificationList(
                            notifications = uiState.filteredNotifications,
                            onNotificationClick = { notification ->
                                viewModel.onEvent(NotificationInboxEvent.NotificationClicked(notification))
                                onNotificationClick(notification)
                            },
                            onDelete = { notificationId ->
                                viewModel.onEvent(NotificationInboxEvent.ShowDeleteConfirmation(notificationId))
                            }
                        )
                    }
                }
            }

            // Error snackbar
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(NotificationInboxEvent.ClearError) }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = { viewModel.onEvent(NotificationInboxEvent.ConfirmDelete) },
            onDismiss = { viewModel.onEvent(NotificationInboxEvent.DismissDeleteConfirmation) }
        )
    }
}

/**
 * Top app bar with title and actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationInboxTopBar(
    unreadCount: Int,
    onBackClick: () -> Unit,
    onClearAll: () -> Unit,
    hasNotifications: Boolean
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Notifications")
                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge {
                        Text("$unreadCount")
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (hasNotifications) {
                IconButton(onClick = onClearAll) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Filter tabs for notification types
 */
@Composable
private fun NotificationFilterTabs(
    selectedFilter: NotificationFilter,
    unreadCount: Int,
    onFilterSelected: (NotificationFilter) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = NotificationFilter.values().indexOf(selectedFilter),
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NotificationFilter.values().forEach { filter ->
            Tab(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(filter.displayName)
                        // Show unread count badge on unread filter
                        if (filter == NotificationFilter.UNREAD && unreadCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge {
                                Text("$unreadCount")
                            }
                        }
                    }
                }
            )
        }
    }
}

/**
 * List of notifications
 */
@Composable
private fun NotificationList(
    notifications: List<NotificationData>,
    onNotificationClick: (NotificationData) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = notifications,
            key = { it.id }
        ) { notification ->
            SwipeableNotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification) },
                onDelete = { onDelete(notification.id) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

/**
 * Loading state view
 */
@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading notifications...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state view
 */
@Composable
private fun EmptyStateView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Notifications will appear here when you receive them",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Delete confirmation dialog
 */
@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Delete Notification?")
        },
        text = {
            Text("This notification will be permanently deleted.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
