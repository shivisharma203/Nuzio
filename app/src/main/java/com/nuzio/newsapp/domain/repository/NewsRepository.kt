package com.nuzio.newsapp.domain.repository

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle

interface NewsRepository {
    suspend fun getTopHeadlines(
        country: String = "us",
        category: String? = null
    ): Resource<List<Any>>
    
    suspend fun searchNews(
        query: String,
        language: String = "en",
        sortBy: String = "publishedAt"
    ): Resource<List<NewsArticle>>
}
