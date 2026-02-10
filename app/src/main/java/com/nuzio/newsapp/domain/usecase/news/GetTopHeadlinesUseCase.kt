package com.nuzio.newsapp.domain.usecase

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.repository.NewsRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for fetching top news headlines.
 *
 * Encapsulates the business logic for retrieving top headlines from the repository
 * with optional country and category filtering. This use case provides a clean
 * interface for ViewModels to request headline data without concerning themselves
 * with repository implementation details or data source coordination.
 *
 * The use case follows the command pattern through the invoke operator function,
 * allowing it to be called like a regular function while maintaining the benefits
 * of dependency injection and testability that come from using a class.
 */
class GetTopHeadlinesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    /**
     * Retrieves top headlines with optional filtering.
     *
     * @param country ISO 3166-1 alpha-2 country code for headline filtering.
     *                Defaults to "us" for United States headlines.
     * @param category Optional category filter such as "business", "technology",
     *                 "sports", etc. When null, returns headlines from all categories.
     * @return Resource wrapper containing either the list of articles on success,
     *         or error information if the operation fails.
     */
    suspend operator fun invoke(
        country: String = "us",
        category: String? = null
    ): Resource<List<NewsArticle>> {
        Timber.d("📋 GetTopHeadlinesUseCase executing: country=$country, category=${category ?: "all"}")

        val result = newsRepository.getTopHeadlines(
            country = country,
            category = category
        )

        when (result) {
            is Resource.Success -> {
                Timber.d("✅ Use case completed successfully: ${result.data.size} articles")
            }
            is Resource.Error -> {
                Timber.e(result.exception, "❌ Use case failed: ${result.message}")
            }
            is Resource.Loading -> {
                Timber.d("🔄 Use case loading...")
            }
        }

        return result as Resource<List<NewsArticle>>
    }
}