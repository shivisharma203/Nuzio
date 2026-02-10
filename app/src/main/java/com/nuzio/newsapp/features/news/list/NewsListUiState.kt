package com.nuzio.newsapp.features.news.list


import com.nuzio.newsapp.core.ui.BaseUiState
import com.nuzio.newsapp.domain.model.NewsArticle

/**
 * UI state for the news list screen.
 *
 * Represents all possible states the news list screen can be in.
 * This immutable data class ensures predictable state updates and
 * makes it easy to test different screen configurations.
 */
data class NewsListUiState(
    val articles: List<NewsArticle> = emptyList(),
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val errorMessage: String? = null,
    val isEmpty: Boolean = false,
    val searchQuery: String = ""
) : BaseUiState(isLoading, isRefreshing, errorMessage) {
    
    /**
     * Returns true if the screen should show the loading indicator.
     */
    fun shouldShowLoading(): Boolean = isLoading && articles.isEmpty()
    
    /**
     * Returns true if the screen should show the error view.
     */
    fun shouldShowError(): Boolean = errorMessage != null && articles.isEmpty()
    
    /**
     * Returns true if the screen should show the empty state.
     */
    fun shouldShowEmpty(): Boolean = isEmpty && articles.isEmpty() && errorMessage == null && !isLoading
}
