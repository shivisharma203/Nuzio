package com.nuzio.newsapp.data.local

import androidx.room.*
import com.nuzio.newsapp.data.local.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: NewsArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(articles: List<NewsArticleEntity>)

    @Query("SELECT * FROM news_articles ORDER BY published_at DESC")
    suspend fun getAllNews(): List<NewsArticleEntity>

    @Query("SELECT * FROM news_articles ORDER BY published_at DESC")
    fun getAllNewsFlow(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE id = :articleId")
    suspend fun getArticleById(articleId: String): NewsArticleEntity?

    @Query("SELECT * FROM news_articles WHERE cached_at >= :timestamp ORDER BY published_at DESC")
    suspend fun getArticlesCachedAfter(timestamp: Long): List<NewsArticleEntity>

    @Delete
    suspend fun deleteArticle(article: NewsArticleEntity)

    @Query("DELETE FROM news_articles WHERE cached_at < :timestamp")
    suspend fun deleteArticlesOlderThan(timestamp: Long)

    @Query("DELETE FROM news_articles")
    suspend fun clearAllNews()

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getArticleCount(): Int
}