package com.nuzio.newsapp.features.news.list


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents news sections corresponding to News API categories.
 *
 * Each section maps to a specific News API category parameter and provides
 * UI metadata for display in the section navigation tabs. The section definitions
 * align with News API's supported categories while providing user-friendly
 * display names and icons for the Material Design 3 interface.
 */
enum class NewsSection(
    val displayName: String,
    val apiCategory: String?,
    val icon: ImageVector,
    val description: String
) {
    TOP_STORIES(
        displayName = "Top Stories",
        apiCategory = null, // null means general news without category filter
        icon = Icons.Default.Star,
        description = "Breaking news and trending stories"
    ),

    WORLD(
        displayName = "World",
        apiCategory = "general",
        icon = Icons.Default.Public,
        description = "International news and global events"
    ),

    POLITICS(
        displayName = "Politics",
        apiCategory = "general", // News API doesn't have dedicated politics category
        icon = Icons.Default.AccountBalance,
        description = "Political news and government updates"
    ),

    BUSINESS(
        displayName = "Business",
        apiCategory = "business",
        icon = Icons.Default.TrendingUp,
        description = "Business news, markets, and economy"
    ),

    TECHNOLOGY(
        displayName = "Technology",
        apiCategory = "technology",
        icon = Icons.Default.Computer,
        description = "Tech news, startups, and innovation"
    ),

    HEALTH(
        displayName = "Health",
        apiCategory = "health",
        icon = Icons.Default.HealthAndSafety,
        description = "Health news, medical research, and wellness"
    ),

    ENTERTAINMENT(
        displayName = "Entertainment",
        apiCategory = "entertainment",
        icon = Icons.Default.Movie,
        description = "Entertainment news, movies, and celebrities"
    ),

    SPORTS(
        displayName = "Sports",
        apiCategory = "sports",
        icon = Icons.Default.SportsBasketball,
        description = "Sports news, scores, and highlights"
    ),

    SCIENCE(
        displayName = "Science",
        apiCategory = "science",
        icon = Icons.Default.Science,
        description = "Science news, research, and discoveries"
    );

    companion object {
        /**
         * Returns the default section shown on app launch.
         */
        fun getDefault(): NewsSection = TOP_STORIES

        /**
         * Finds a section by its display name (case-insensitive).
         * Used for deep link parsing and navigation.
         */
        fun fromDisplayName(name: String): NewsSection? {
            return values().find {
                it.displayName.equals(name, ignoreCase = true)
            }
        }

        /**
         * Finds a section by its API category parameter.
         * Used for API response mapping.
         */
        fun fromApiCategory(category: String?): NewsSection {
            return values().find {
                it.apiCategory?.equals(category, ignoreCase = true) == true
            } ?: TOP_STORIES
        }
    }
}