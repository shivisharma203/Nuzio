package com.nuzio.newsapp.features.news.list

import androidx.lifecycle.viewModelScope
import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.usecase.GetTopHeadlinesUseCase
import com.nuzio.newsapp.domain.usecase.SearchNewsUseCase
import com.nuzio.newsapp.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the news list screen.
 *
 * Manages the state of the news list and handles all user interactions.
 * Uses the BaseViewModel to inherit standard state management capabilities.
 *
 * This ViewModel demonstrates the MVVM pattern with unidirectional data flow:
 * - UI emits events through onEvent()
 * - ViewModel processes events and updates state
 * - UI observes state changes and recomposes
 *
 * @param getTopHeadlinesUseCase Use case for fetching top headlines
 * @param searchNewsUseCase Use case for searching news
 */
@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val searchNewsUseCase: SearchNewsUseCase
) : BaseViewModel<NewsListUiState>(NewsListUiState()) {

    init {
        // Load news automatically when ViewModel is created
        loadTopHeadlines()
    }

    /**
     * Handles all user events from the UI.
     *
     * This is the single entry point for all user interactions,
     * ensuring consistent event handling and making testing easier.
     *
     * @param event The event to handle
     */
    fun onEvent(event: NewsListEvent) {
        Timber.d("🎯 Event received: ${event::class.simpleName}")
        
        when (event) {
            is NewsListEvent.LoadNews -> loadTopHeadlines()
            is NewsListEvent.Refresh -> loadTopHeadlines(isRefresh = true)
            is NewsListEvent.Retry -> loadTopHeadlines()
            is NewsListEvent.Search -> searchNews(event.query)
            is NewsListEvent.ArticleClick -> handleArticleClick(event.article)
            is NewsListEvent.BookmarkArticle -> bookmarkArticle(event.article)
        }
    }

    /**
     * Loads top headlines from the news API.
     *
     * Updates the UI state to show loading, then fetches articles
     * and updates state based on the result (success or error).
     *
     * @param isRefresh True if this is a pull-to-refresh action
     */
    private fun loadTopHeadlines(isRefresh: Boolean = false) {
        viewModelScope.launch {
            setState {
                copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null
                )
            }

            when (val result = getTopHeadlinesUseCase()) {
                is Resource.Success -> {
                    setState {
                        copy(
                            articles = result.data,
                            isLoading = false,
                            isRefreshing = false,
                            isEmpty = result.data.isEmpty()
                        )
                    }
                }
                is Resource.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    // Loading state already set above
                }
            }
        }
    }

    /**
     * Searches for news articles matching the query.
     *
     * @param query The search query
     */
    private fun searchNews(query: String) {
        if (query.isBlank()) {
            loadTopHeadlines()
            return
        }
        
        Timber.d("🔍 Searching news: $query")
        
        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    errorMessage = null,
                    searchQuery = query
                )
            }

            when (val result = searchNewsUseCase(query)) {
                is Resource.Success -> {
                    Timber.d("✅ Found ${result.data.size} articles")
                    setState {
                        copy(
                            articles = result.data,
                            isLoading = false,
                            isEmpty = result.data.isEmpty(),
                            errorMessage = null
                        )
                    }
                }
                
                is Resource.Error -> {
                    Timber.e(result.exception, "❌ Search error")
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Search failed"
                        )
                    }
                }
                
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }

    /**
     * Handles article click event.
     *
     * In a real app, this would navigate to the detail screen.
     * For now, it just logs the event.
     *
     * @param article The clicked article
     */
    private fun handleArticleClick(article: NewsArticle) {
        Timber.d("📰 Article clicked: ${article.title}")
        // TODO: Navigate to detail screen
    }

    /**
     * Bookmarks an article for later reading.
     *
     * @param article The article to bookmark
     */
    private fun bookmarkArticle(article: NewsArticle) {
        Timber.d("🔖 Bookmarking article: ${article.title}")
        // TODO: Implement bookmark functionality
    }
}
