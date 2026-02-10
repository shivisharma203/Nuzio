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
import timber.log.Timber
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApiService,
    private val newsDao: NewsDao
) : NewsRepository {

    override suspend fun getTopHeadlines(
        country: String,
        category: String?
    ): Resource<List<NewsArticle>> {
        Timber.d("📡 Fetching top headlines (country: $country, category: ${category ?: "all"})")

        val cachedArticles = try {
            newsDao.getAllNews().toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Failed to read from cache")
            emptyList()
        }

        if (cachedArticles.isNotEmpty()) {
            Timber.d("💾 Found ${cachedArticles.size} cached articles available for fallback")
        }

        return when (val result = safeApiCall {
            // AuthInterceptor automatically adds API key
            val response = newsApi.getTopHeadlinesDto(
                country = country,
                category = category
            )
            response.toDomain()
        }) {
            is Resource.Success -> {
                Timber.d("✅ Received ${result.data.size} fresh articles from API")
                updateCache(result.data)
                result
            }

            is Resource.Error -> {
                if (cachedArticles.isNotEmpty()) {
                    Timber.w("⚠️ Network request failed but returning ${cachedArticles.size} cached articles")
                    Timber.w("Network error: ${result.message}")
                    Resource.Success(cachedArticles)
                } else {
                    Timber.e(result.exception, "❌ Network failed and no cache available")
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

        // AuthInterceptor automatically adds API key
        val response = newsApi.searchNewsDto(
            query = query,
            language = language,
            sortBy = sortBy
        )

        val articles = response.toDomain()
        Timber.d("✅ Found ${articles.size} articles matching search")

        articles
    }

    private suspend fun updateCache(articles: List<NewsArticle>) {
        try {
            newsDao.clearAllNews()
            newsDao.insertNews(articles.toEntities())
            Timber.d("💾 Successfully cached ${articles.size} articles")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to update cache: ${e.message}")
        }
    }
}