package com.nuzio.newsapp.domain.usecase

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.repository.NewsRepository
import com.nuzio.newsapp.features.news.list.NewsSection
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for fetching top headlines for a specific news section.
 *
 * Coordinates the retrieval of section-specific headlines through the
 * repository layer, applying business logic validation and logging.
 * The use case pattern encapsulates this specific business operation,
 * making it reusable across different presentation layer components.
 */
class GetTopHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    /**
     * Fetches top headlines for the specified section and country.
     *
     * @param section The news section to fetch headlines for (default: Top Stories)
     * @param country ISO 3166-1 alpha-2 country code (default: "us")
     * @return Resource containing the list of articles or an error
     */
    suspend operator fun invoke(
        section: NewsSection = NewsSection.getDefault(),
        country: String = "us"
    ): Resource<List<NewsArticle>> {
        Timber.d("📰 GetTopHeadlinesUseCase: Fetching ${section.displayName} headlines for $country")

        // Validate country code format (basic validation)
        if (country.length != 2) {
            Timber.w("⚠️ Invalid country code format: $country, using default 'us'")
            return repository.getTopHeadlines(section, "us")
        }

        return repository.getTopHeadlines(section, country)
    }
}