package com.nuzio.newsapp.data.repository

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.core.network.safeApiCall
import com.nuzio.newsapp.data.local.NewsDao
import com.nuzio.newsapp.data.local.entity.toDomain
import com.nuzio.newsapp.data.local.entity.toEntities
import com.nuzio.newsapp.data.remote.NewsApiService
import com.nuzio.newsapp.data.remote.dto.toDomain
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.repository.NewsRepository
import com.nuzio.newsapp.features.news.list.NewsSection
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of NewsRepository with section-based caching strategy.
 *
 * Implements offline-first pattern where each section maintains its own
 * cache, enabling section-specific offline access and reducing unnecessary
 * network requests when switching between sections.
 */
class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApiService,
    private val newsDao: NewsDao
) : NewsRepository {

    override suspend fun getTopHeadlines(
        section: NewsSection,
        country: String
    ): Resource<List<NewsArticle>> {
        Timber.d("📡 Fetching headlines for section: ${section.displayName}, country: $country")

        val cachedArticles = try {
            newsDao.getNewsBySection(section.name).toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Failed to read section cache for ${section.displayName}")
            emptyList()
        }

        if (cachedArticles.isNotEmpty()) {
            Timber.d("💾 Found ${cachedArticles.size} cached articles for ${section.displayName}")
        }

        return when (val result = safeApiCall {
            val response = newsApi.getTopHeadlinesDto(
                country = country,
                category = section.apiCategory
            )
            response.toDomain()
        }) {
            is Resource.Success -> {
                Timber.d("✅ Received ${result.data.size} fresh articles for ${section.displayName}")
                updateSectionCache(section, result.data)
                result
            }

            is Resource.Error -> {
                if (cachedArticles.isNotEmpty()) {
                    Timber.w("⚠️ Network failed for ${section.displayName} but returning ${cachedArticles.size} cached articles")
                    Timber.w("Network error: ${result.message}")
                    Resource.Success(cachedArticles)
                } else {
                    Timber.e(result.exception, "❌ Network failed for ${section.displayName} and no cache available")
                    result
                }
            }

            is Resource.Loading -> result
        }
    }

    override suspend fun searchNews(
        query: String,
        language: String,
        sortBy: String
    ): Resource<List<NewsArticle>> = safeApiCall {
        Timber.d("🔍 Searching news: query='$query', language=$language, sortBy=$sortBy")

        val response = newsApi.searchNewsDto(
            query = query,
            language = language,
            sortBy = sortBy
        )

        val articles = response.toDomain()
        Timber.d("✅ Found ${articles.size} articles matching search")

        articles
    }

    private suspend fun updateSectionCache(section: NewsSection, articles: List<NewsArticle>) {
        try {
            newsDao.clearSection(section.name)
            newsDao.insertNews(articles.toEntities(section))
            Timber.d("💾 Successfully cached ${articles.size} articles for ${section.displayName}")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to update cache for ${section.displayName}: ${e.message}")
        }
    }
}