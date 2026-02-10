package com.nuzio.newsapp.features.news.list

import com.nuzio.newsapp.domain.model.NewsArticle

/**
 * Sealed class representing all possible user interactions on the news list screen.
 *
 * Using a sealed class ensures type-safety and makes it impossible to forget
 * handling any event type in the ViewModel, providing compile-time guarantees
 * about event processing completeness.
 */
sealed class NewsListEvent {

    /**
     * User requested to load news for the current section.
     */
    data object LoadNews : NewsListEvent()

    /**
     * User initiated pull-to-refresh gesture.
     */
    data object Refresh : NewsListEvent()

    /**
     * User tapped retry button after an error.
     */
    data object Retry : NewsListEvent()

    /**
     * User changed to a different news section.
     * @param section The newly selected section to display
     */
    data class SectionChanged(val section: NewsSection) : NewsListEvent()

    /**
     * User performed a search query.
     * @param query The search term entered by the user
     */
    data class Search(val query: String) : NewsListEvent()

    /**
     * User cleared the active search.
     */
    data object ClearSearch : NewsListEvent()

    /**
     * User clicked on a news article.
     * @param article The article that was clicked
     */
    data class ArticleClick(val article: NewsArticle) : NewsListEvent()

    /**
     * User toggled bookmark status of an article (future implementation).
     * @param article The article to bookmark/unbookmark
     */
    data class BookmarkArticle(val article: NewsArticle) : NewsListEvent()
}