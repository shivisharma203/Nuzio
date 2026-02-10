package com.nuzio.newsapp.data.local

import androidx.room.*
import com.nuzio.newsapp.data.local.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    /**
     * Inserts a single article, replacing on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: NewsArticleEntity)

    /**
     * Inserts multiple articles, replacing on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(articles: List<NewsArticleEntity>)

    /**
     * Retrieves all cached articles ordered by publication date.
     */
    @Query("SELECT * FROM news_articles ORDER BY published_at DESC")
    suspend fun getAllNews(): List<NewsArticleEntity>

    /**
     * Retrieves articles for a specific section ordered by publication date.
     * Enables section-specific offline access and caching.
     */
    @Query("SELECT * FROM news_articles WHERE section = :section ORDER BY published_at DESC")
    suspend fun getNewsBySection(section: String): List<NewsArticleEntity>

    /**
     * Observes articles for a specific section as a Flow.
     * Enables reactive UI updates when section cache changes.
     */
    @Query("SELECT * FROM news_articles WHERE section = :section ORDER BY published_at DESC")
    fun getNewsBySectionFlow(section: String): Flow<List<NewsArticleEntity>>

    /**
     * Retrieves all cached articles as a Flow for reactive updates.
     */
    @Query("SELECT * FROM news_articles ORDER BY published_at DESC")
    fun getAllNewsFlow(): Flow<List<NewsArticleEntity>>

    /**
     * Retrieves a specific article by ID.
     */
    @Query("SELECT * FROM news_articles WHERE id = :articleId")
    suspend fun getArticleById(articleId: String): NewsArticleEntity?

    /**
     * Retrieves articles cached after a specific timestamp.
     */
    @Query("SELECT * FROM news_articles WHERE cached_at > :timestamp ORDER BY published_at DESC")
    suspend fun getArticlesCachedAfter(timestamp: Long): List<NewsArticleEntity>

    /**
     * Deletes a specific article.
     */
    @Query("DELETE FROM news_articles WHERE id = :articleId")
    suspend fun deleteArticle(articleId: String)

    /**
     * Deletes articles older than a specific timestamp.
     * Used for cache cleanup and maintenance.
     */
    @Query("DELETE FROM news_articles WHERE cached_at < :timestamp")
    suspend fun deleteArticlesOlderThan(timestamp: Long)

    /**
     * Deletes all articles from a specific section.
     * Used when refreshing section-specific cache.
     */
    @Query("DELETE FROM news_articles WHERE section = :section")
    suspend fun clearSection(section: String)

    /**
     * Deletes all cached articles.
     */
    @Query("DELETE FROM news_articles")
    suspend fun clearAllNews()

    /**
     * Returns the total count of cached articles.
     */
    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getArticleCount(): Int

    /**
     * Returns the count of cached articles for a specific section.
     */
    @Query("SELECT COUNT(*) FROM news_articles WHERE section = :section")
    suspend fun getArticleCountBySection(section: String): Int
}