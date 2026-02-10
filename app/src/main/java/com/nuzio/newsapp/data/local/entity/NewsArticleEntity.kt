package com.nuzio.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a cached news article.
 *
 * Includes section field to enable section-specific caching and querying,
 * allowing the app to maintain separate caches for different news sections
 * and support offline access to section-filtered content.
 */
@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "source_id")
    val sourceId: String?,

    @ColumnInfo(name = "source_name")
    val sourceName: String,

    @ColumnInfo(name = "author")
    val author: String?,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "url_to_image")
    val urlToImage: String?,

    @ColumnInfo(name = "published_at")
    val publishedAt: String,

    @ColumnInfo(name = "content")
    val content: String?,

    @ColumnInfo(name = "section")
    val section: String, // NEW - Stores NewsSection enum name

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)