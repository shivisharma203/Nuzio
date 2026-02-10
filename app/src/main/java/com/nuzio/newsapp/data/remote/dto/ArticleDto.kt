package com.nuzio.newsapp.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO representing a single news article from the API.
 *
 * Fields marked as nullable match the API behavior where certain
 * fields may be absent or null in the response.
 */
@Serializable
data class ArticleDto(
    val source: SourceDto,
    val author: String? = null,
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null,
    val publishedAt: String,
    val content: String? = null
)