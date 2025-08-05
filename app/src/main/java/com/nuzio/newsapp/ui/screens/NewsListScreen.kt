package com.nuzio.newsapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nuzio.newsapp.data.model.NewsItem
import com.nuzio.newsapp.ui.viewmodel.AuthViewModel
import com.nuzio.newsapp.ui.viewmodel.NewsViewModel
import com.nuzio.newsapp.ui.viewmodel.UiState
import kotlinx.coroutines.launch

/**
 * Main screen that displays a list of news items and provides a logout button.
 *
 * @param viewModel ViewModel that manages the news list and its UI state.
 * @param authViewModel ViewModel for authentication logic, like checking current user.
 * @param onLogoutSuccess Callback triggered after a successful logout.
 * @param onNewsItemClick Callback triggered when a news item is clicked (navigates to detail screen).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    viewModel: NewsViewModel,
    authViewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit,
    onNewsItemClick: (NewsItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState() // observe UI state (Loading, Success, etc.)
    val scope = rememberCoroutineScope()

    // UI flags for logout operation and error messages
    var isLoggingOut by remember { mutableStateOf(false) }
    var logoutError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuzio") },
                actions = {
                    // Logout button (shows progress when logging out)
                    IconButton(
                        onClick = {
                            logoutError = null
                            isLoggingOut = true
                            scope.launch {
                                try {
                                    onLogoutSuccess() // trigger logout (usually signs out & navigates)
                                    authViewModel.checkCurrentUser() // optional: update user state
                                } catch (e: Exception) {
                                    logoutError = e.localizedMessage ?: "Logout failed"
                                } finally {
                                    isLoggingOut = false
                                }
                            }
                        },
                        enabled = !isLoggingOut
                    ) {
                        if (isLoggingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Show logout error message if exists
            logoutError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Animated loading spinner
            AnimatedVisibility(
                visible = uiState is UiState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            // Animated error message
            AnimatedVisibility(
                visible = uiState is UiState.Error,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Success state: show news list
            if (uiState is UiState.Success) {
                NewsList(
                    news = (uiState as UiState.Success).newsList,
                    onItemClick = onNewsItemClick
                )
            }

            // Empty state UI
            if (uiState is UiState.Empty) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No news available",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

/**
 * Displays a list of news items in a vertical scrollable layout.
 *
 * @param news The list of [NewsItem] to display.
 * @param onItemClick Callback when a specific item is clicked.
 */
@Composable
fun NewsList(news: List<NewsItem>, onItemClick: (NewsItem) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(news) { item ->
            NewsListItem(item = item, onClick = { onItemClick(item) })
        }
    }
}

/**
 * Renders a single news item card.
 *
 * @param item The news data model containing title, thumbnail, and date.
 * @param onClick Action triggered when this card is clicked.
 */
@Composable
fun NewsListItem(item: NewsItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Optional image (if available)
            item.thumbnail?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
            }

            // Title and publication date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.pubDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
