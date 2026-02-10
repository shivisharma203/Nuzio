package com.nuzio.newsapp.data.remote

import com.nuzio.newsapp.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface defining News API endpoints.
 *
 * Authentication is handled transparently by AuthInterceptor,
 * eliminating the need for explicit API key parameters in method signatures.
 * The interceptor automatically adds the API key as a query parameter to all
 * outgoing requests, centralizing credential management within the networking layer.
 */
interface NewsApiService {

    /**
     * Fetches top headlines from the News API.
     *
     * @param country ISO 3166-1 alpha-2 country code for news sources
     * @param category News category filter (business, entertainment, general, health, science, sports, technology)
     * @return NewsResponseDto containing the list of articles
     */
    @GET("top-headlines")
    suspend fun getTopHeadlinesDto(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null
    ): NewsResponseDto

    /**
     * Searches for news articles matching the specified query.
     *
     * @param query Keywords or phrases to search for in article titles and bodies
     * @param language ISO 639-1 language code for article filtering
     * @param sortBy Sort order for results (relevancy, popularity, publishedAt)
     * @return NewsResponseDto containing the list of matching articles
     */
    @GET("everything")
    suspend fun searchNewsDto(
        @Query("q") query: String,
        @Query("language") language: String = "en",
        @Query("sortBy") sortBy: String = "publishedAt"
    ): NewsResponseDto
}