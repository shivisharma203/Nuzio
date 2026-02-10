package com.nuzio.newsapp.features.news.list

import com.nuzio.newsapp.core.ui.BaseUiState
import com.nuzio.newsapp.domain.model.NewsArticle

/**
 * Represents the complete state of the news list screen with section support.
 *
 * This immutable data class captures all possible configurations including
 * the current active section, article list, loading states, error messages,
 * and search query. Section-aware caching enables each section to maintain
 * its own cached article list for offline access.
 */
data class NewsListUiState(
    val currentSection: NewsSection = NewsSection.getDefault(),
    val articles: List<NewsArticle> = emptyList(),
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val errorMessage: String? = null,
    val isEmpty: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,

    // Section-specific loading states for better UX
    val loadingSections: Set<NewsSection> = emptySet()
) : BaseUiState(isLoading, isRefreshing, errorMessage) {

    /**
     * Determines whether to show the main loading indicator.
     * Shows loading only when actively loading and no articles are displayed.
     */
    fun shouldShowLoading(): Boolean = isLoading && articles.isEmpty() && !isRefreshing

    /**
     * Determines whether to show the error view.
     * Shows error only when error exists and no articles are displayed.
     */
    fun shouldShowError(): Boolean = errorMessage != null && articles.isEmpty()

    /**
     * Determines whether to show the empty state view.
     * Shows empty state when no error, not loading, and no articles exist.
     */
    fun shouldShowEmpty(): Boolean = isEmpty && articles.isEmpty() && !isLoading && errorMessage == null

    /**
     * Checks if a specific section is currently loading.
     */
    fun isSectionLoading(section: NewsSection): Boolean = section in loadingSections

    /**
     * Returns the display title for the current view state.
     */
    fun getDisplayTitle(): String = when {
        isSearchActive -> "Search Results"
        else -> currentSection.displayName
    }
}