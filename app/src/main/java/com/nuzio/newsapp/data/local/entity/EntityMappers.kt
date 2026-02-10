package com.nuzio.newsapp.data.local.entity

import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.features.news.list.NewsSection

/**
 * Converts NewsArticleEntity to domain NewsArticle model.
 */
fun NewsArticleEntity.toDomain(): NewsArticle {
    return NewsArticle(
        id = id,
        source = NewsArticle.Source(
            id = sourceId,
            name = sourceName
        ),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

/**
 * Converts domain NewsArticle to NewsArticleEntity for database storage.
 * Requires section parameter to associate article with specific section cache.
 */
fun NewsArticle.toEntity(section: NewsSection): NewsArticleEntity {
    return NewsArticleEntity(
        id = id,
        sourceId = source.id,
        sourceName = source.name,
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        section = section.name, // Store enum name as string
        cachedAt = System.currentTimeMillis()
    )
}

/**
 * Converts list of entities to domain models.
 */
fun List<NewsArticleEntity>.toDomain(): List<NewsArticle> {
    return map { it.toDomain() }
}

/**
 * Converts list of domain models to entities for a specific section.
 */
fun List<NewsArticle>.toEntities(section: NewsSection): List<NewsArticleEntity> {
    return map { it.toEntity(section) }
}