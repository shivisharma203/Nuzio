package com.nuzio.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a cached news article.
 *
 * This entity exists in the data layer and represents the database
 * table structure, independent of both the domain model and API DTOs.
 *
 * All fields use explicit @ColumnInfo annotations to ensure column
 * names are clearly defined and prevent any naming ambiguities.
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

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)