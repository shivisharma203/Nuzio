package com.nuzio.newsapp.features.news.list

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nuzio.newsapp.core.ui.components.EnhancedArticleCard
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.features.news.compnents.SectionTabRow


/**
 * Main news list screen with section-based navigation.
 *
 * Displays a scrollable tab row for section selection followed by
 * a list of news articles for the selected section. Implements
 * pull-to-refresh, search functionality, and proper loading/error states.
 *
 * @param onArticleClick Callback invoked when user taps an article
 * @param viewModel ViewModel managing screen state and business logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onArticleClick: (NewsArticle) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Load initial news on first composition
    LaunchedEffect(Unit) {
        viewModel.onEvent(NewsListEvent.LoadNews)
    }

    Scaffold(
        topBar = {
            NewsListTopBar(
                searchQuery = state.searchQuery,
                isSearchActive = state.isSearchActive,
                onSearchQueryChanged = { query ->
                    viewModel.onEvent(NewsListEvent.Search(query))
                },
                onSearchClear = {
                    viewModel.onEvent(NewsListEvent.ClearSearch)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Section tabs (hidden during search)
            AnimatedVisibility(
                visible = !state.isSearchActive,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SectionTabRow(
                    selectedSection = state.currentSection,
                    onSectionSelected = { section ->
                        viewModel.onEvent(NewsListEvent.SectionChanged(section))
                    }
                )
            }

            // Main content area
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
                onRefresh = { viewModel.onEvent(NewsListEvent.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = when {
                        state.shouldShowLoading() -> ScreenState.Loading
                        state.shouldShowError() -> ScreenState.Error(state.errorMessage ?: "Unknown error")
                        state.shouldShowEmpty() -> ScreenState.Empty
                        else -> ScreenState.Success
                    },
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "content_animation"
                ) { screenState ->
                    when (screenState) {
                        is ScreenState.Loading -> LoadingView()
                        is ScreenState.Error -> ErrorView(
                            message = screenState.message,
                            onRetry = { viewModel.onEvent(NewsListEvent.Retry) }
                        )
                        is ScreenState.Empty -> EmptyView(
                            message = if (state.isSearchActive) {
                                "No articles found for \"${state.searchQuery}\""
                            } else {
                                "No news available in ${state.currentSection.displayName}"
                            }
                        )
                        is ScreenState.Success -> NewsArticleList(
                            articles = state.articles,
                            onArticleClick = { article ->
                                viewModel.onEvent(NewsListEvent.ArticleClick(article))
                                onArticleClick(article)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top app bar with search functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsListTopBar(
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClear: () -> Unit
) {
    var showSearchBar by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (showSearchBar) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    onSearch = { /* Search triggered automatically via debounce */ },
                    active = false,
                    onActiveChange = { },
                    placeholder = { Text("Search news...") },
                    leadingIcon = {
                        IconButton(onClick = {
                            showSearchBar = false
                            onSearchClear()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close search")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onSearchClear) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { }
            } else {
                Text("Nuzio News")
            }
        },
        actions = {
            if (!showSearchBar) {
                IconButton(onClick = { showSearchBar = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        }
    )
}

/**
 * List of news articles with smooth animations.
 */
@Composable
private fun NewsArticleList(
    articles: List<NewsArticle>,
    onArticleClick: (NewsArticle) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = articles,
            key = { it.id }
        ) { article ->
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            EnhancedArticleCard(
                article = article,
                onClick = { onArticleClick(article) },
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = tween(300)
                )
            )
        }
    }
}

/**
 * Loading state view.
 */
@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading news...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error state view with retry button.
 */
@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

/**
 * Empty state view.
 */
@Composable
private fun EmptyView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Article,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Sealed class representing different screen states for animated transitions.
 */
private sealed class ScreenState {
    data object Loading : ScreenState()
    data class Error(val message: String) : ScreenState()
    data object Empty : ScreenState()
    data object Success : ScreenState()
}