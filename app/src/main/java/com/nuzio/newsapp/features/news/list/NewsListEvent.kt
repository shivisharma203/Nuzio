package com.nuzio.newsapp.features.news.list

import com.nuzio.newsapp.domain.model.NewsArticle

/**
 * Sealed class representing all possible user interactions and events
 * that can occur on the news list screen.
 *
 * Using a sealed class ensures type-safety and makes it impossible to
 * forget handling any event type.
 */
sealed class NewsListEvent {
    /**
     * User requested to load news (initial load).
     */
    data object LoadNews : NewsListEvent()
    
    /**
     * User pulled to refresh the news list.
     */
    data object Refresh : NewsListEvent()
    
    /**
     * User wants to retry after an error.
     */
    data object Retry : NewsListEvent()
    
    /**
     * User searched for news with a query.
     *
     * @param query The search query entered by the user
     */
    data class Search(val query: String) : NewsListEvent()
    
    /**
     * User clicked on a news article.
     *
     * @param article The article that was clicked
     */
    data class ArticleClick(val article: NewsArticle) : NewsListEvent()
    
    /**
     * User wants to bookmark/save an article.
     *
     * @param article The article to bookmark
     */
    data class BookmarkArticle(val article: NewsArticle) : NewsListEvent()
}
