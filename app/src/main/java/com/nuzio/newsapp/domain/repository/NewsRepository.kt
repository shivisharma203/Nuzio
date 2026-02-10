package com.nuzio.newsapp.domain.repository

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.features.news.list.NewsSection

/**
 * Repository interface for news data access with section support.
 *
 * Defines the contract for news operations including fetching top headlines
 * by section, searching news, and managing section-specific caches. This
 * abstraction enables dependency inversion where use cases depend on
 * interfaces rather than concrete implementations.
 */
interface NewsRepository {

    /**
     * Fetches top headlines for a specific section with offline-first strategy.
     *
     * Implementation should:
     * 1. Check section-specific cache first
     * 2. Fetch from network if cache is stale or empty
     * 3. Update section cache on successful network fetch
     * 4. Fall back to section cache if network fails
     *
     * @param section The news section to fetch headlines for
     * @param country ISO 3166-1 alpha-2 country code
     * @return Resource containing list of articles or error
     */
    suspend fun getTopHeadlines(
        section: NewsSection,
        country: String = "us"
    ): Resource<List<NewsArticle>>

    /**
     * Searches for news articles matching the query across all sections.
     *
     * @param query Search keywords or phrases
     * @param language ISO 639-1 language code
     * @param sortBy Sort order (relevancy, popularity, publishedAt)
     * @return Resource containing list of matching articles or error
     */
    suspend fun searchNews(
        query: String,
        language: String = "en",
        sortBy: String = "publishedAt"
    ): Resource<List<NewsArticle>>
}