
package com.nuzio.newsapp.data

import com.nuzio.newsapp.data.entities.NewsItemEntity
import com.nuzio.newsapp.data.local.NewsDao
import com.nuzio.newsapp.data.model.NewsItem
import com.nuzio.newsapp.data.remote.NewsApiService
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val apiService: NewsApiService,
    private val newsDao: NewsDao
) {
    suspend fun getNews(): List<NewsItem> {
        return try {
            val response = apiService.getNews()
            if (response.status == "ok") {
                // Cache in Room
                newsDao.clearAll()
                val entities = response.items.map {
                    NewsItemEntity(it.link, it.title, it.thumbnail, it.pubDate)
                }
                newsDao.insertAll(entities)
                response.items
            } else {
                // fallback to DB
                getCachedNews()
            }
        } catch (e: Exception) {
            // fallback to DB
            getCachedNews()
        }
    }

    private suspend fun getCachedNews(): List<NewsItem> {
        return newsDao.getAllNews().map {
            NewsItem(it.title, it.link, it.thumbnail, it.pubDate)
        }
    }
}