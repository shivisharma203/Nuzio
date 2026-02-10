package com.nuzio.newsapp.domain.usecase

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.repository.NewsRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for searching news articles.
 *
 * Handles the business logic for searching news based on a query string.
 * Validates the query and delegates to the repository for actual data fetching.
 *
 * @param newsRepository Repository providing access to news data
 */
class SearchNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    /**
     * Executes the search use case.
     *
     * @param query The search query (must not be blank)
     * @param language Optional language code for search results
     * @param sortBy Optional sorting criteria (relevancy, popularity, publishedAt)
     * @return Resource containing matching articles or error
     */
    suspend operator fun invoke(
        query: String,
        language: String = "en",
        sortBy: String = "publishedAt"
    ): Resource<List<NewsArticle>> {
        Timber.d("🔍 SearchNewsUseCase: query='$query', language=$language")
        
        // Validate query
        if (query.isBlank()) {
            return Resource.Error(
                exception = IllegalArgumentException("Search query cannot be blank"),
                message = "Please enter a search term"
            )
        }
        
        return newsRepository.searchNews(
            query = query,
            language = language,
            sortBy = sortBy
        )
    }
}
