package com.nuzio.newsapp.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO representing the news source in API responses.
 */
@Serializable
data class SourceDto(
    val id: String? = null,
    val name: String
)