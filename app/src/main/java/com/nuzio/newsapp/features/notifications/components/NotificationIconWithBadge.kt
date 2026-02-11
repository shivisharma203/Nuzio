package com.nuzio.newsapp.features.notifications.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.nuzio.newsapp.domain.usecase.notifications.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel for notification badge
 */
@HiltViewModel
class NotificationBadgeViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : ViewModel() {

    val unreadCount: Flow<Int> = getNotificationsUseCase.getUnreadCount()
}

/**
 * Notification icon with unread count badge
 */
@Composable
fun NotificationIconWithBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationBadgeViewModel = hiltViewModel()
) {
    val unreadCount by viewModel.unreadCount.collectAsState(initial = 0)

    Box(modifier = modifier) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }

        AnimatedVisibility(
            visible = unreadCount > 0,
            enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
            exit = scaleOut(animationSpec = tween(300)) + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Badge(
                modifier = Modifier.size(20.dp)
            ) {
                Text(
                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

/**
 * Simplified notification badge for use in other screens
 */
@Composable
fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = count > 0,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Badge {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
