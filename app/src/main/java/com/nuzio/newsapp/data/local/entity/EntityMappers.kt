package com.nuzio.newsapp.data.local.entity

import com.nuzio.newsapp.domain.model.NewsArticle

/**
 * Converts NewsArticleEntity to NewsArticle domain model.
 *
 * Used when reading cached articles from the database to display
 * in the UI while fresh data loads from the network.
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
 * Converts NewsArticle domain model to NewsArticleEntity.
 *
 * Used when caching fresh articles fetched from the network
 * for future offline access.
 */
fun NewsArticle.toEntity(): NewsArticleEntity {
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
        cachedAt = System.currentTimeMillis()
    )
}

/**
 * Extension functions for list transformations.
 */
fun List<NewsArticleEntity>.toDomain(): List<NewsArticle> = map { it.toDomain() }
fun List<NewsArticle>.toEntities(): List<NewsArticleEntity> = map { it.toEntity() }