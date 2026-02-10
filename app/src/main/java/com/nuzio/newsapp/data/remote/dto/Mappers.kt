package com.nuzio.newsapp.data.remote.dto

import com.nuzio.newsapp.domain.model.NewsArticle
import java.util.UUID

/**
 * Extension function to convert ArticleDto to NewsArticle domain model.
 *
 * This transformation isolates the domain layer from API structure changes.
 * The UUID generation ensures each article has a unique identifier even if
 * the API does not provide one.
 */
fun ArticleDto.toDomain(): NewsArticle {
    return NewsArticle(
        id = UUID.randomUUID().toString(),
        source = NewsArticle.Source(
            id = source.id,
            name = source.name
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
 * Extension function to convert a list of ArticleDtos to NewsArticles.
 *
 * This enables clean transformation of the entire article list from
 * API responses to domain models.
 */
fun List<ArticleDto>.toDomain(): List<NewsArticle> {
    return this.map { it.toDomain() }
}

/**
 * Extension function to convert NewsResponseDto to a list of NewsArticles.
 *
 * This extracts the articles from the response wrapper and transforms
 * them to domain models in a single operation.
 */
fun NewsResponseDto.toDomain(): List<NewsArticle> {
    return articles.toDomain()
}