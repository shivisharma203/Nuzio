package com.nuzio.newsapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nuzio.newsapp.data.model.NewsItem
import com.nuzio.newsapp.ui.viewmodel.AuthViewModel
import com.nuzio.newsapp.ui.viewmodel.NewsViewModel
import com.nuzio.newsapp.ui.viewmodel.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    viewModel: NewsViewModel,
    authViewModel:AuthViewModel,
    onLogoutSuccess: () -> Unit
) {
    val state = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoggingOut by remember { mutableStateOf(false) }
    var logoutError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuzio") },
                actions = {
                    IconButton(
                        onClick = {
                            logoutError = null
                            isLoggingOut = true
                            scope.launch {
                                try {
                                    onLogoutSuccess()  // directly call this lambda which signs out via ViewModel
                                    authViewModel.checkCurrentUser() // update auth state (optional if signOut updates it)
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
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(imageVector = Icons.Default.Abc, contentDescription = "Logout")

                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column {
                logoutError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                when (state) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    is UiState.Success -> {
                        NewsList(state.newsList)
                    }

                    UiState.Empty -> {
                        // Show empty state UI here if needed
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "No news available",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsList(news: List<NewsItem>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(news) { item ->
            NewsListItem(item)
        }
    }
}

@Composable
fun NewsListItem(item: NewsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: open details */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (item.thumbnail != null) {
                Image(
                    painter = rememberAsyncImagePainter(item.thumbnail),
                    contentDescription = "News image",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
            }
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

@Preview(showBackground = true)
@Composable
fun PreviewNewsListItem() {
    NewsListItem(
        item = NewsItem(
            title = "Sample News Title",
            link = "https://abc.net.au",
            thumbnail = "https://www.abc.net.au/news/image.jpg",
            pubDate = "2025-06-04"
        )
    )
}
