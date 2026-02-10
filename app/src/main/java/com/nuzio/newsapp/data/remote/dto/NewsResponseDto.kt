package com.nuzio.newsapp.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO representing the News API response wrapper.
 *
 * This matches the exact structure returned by the News API.
 */
@Serializable
data class NewsResponseDto(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleDto>
)