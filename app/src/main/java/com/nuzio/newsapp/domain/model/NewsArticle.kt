package com.nuzio.newsapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsArticle(
    val id: String,
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
) {
    @Serializable
    data class Source(
        val id: String?,
        val name: String
    )

    fun hasImage(): Boolean = !urlToImage.isNullOrBlank()
    fun getAuthorOrDefault(): String = author ?: "Unknown Author"
    fun getDescriptionOrDefault(): String = description ?: "No description available"
}