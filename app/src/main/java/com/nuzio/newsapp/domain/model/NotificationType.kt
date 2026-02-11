package com.nuzio.newsapp.domain.model

/**
 * Represents different types of notifications supported by the app
 */
enum class NotificationType {
    /**
     * Breaking news alerts - high priority, urgent news
     */
    BREAKING_NEWS,
    
    /**
     * Section-specific notifications (e.g., Technology, Sports, etc.)
     */
    SECTION_SPECIFIC,
    
    /**
     * General news updates
     */
    GENERAL,
    
    /**
     * App updates and system notifications
     */
    SYSTEM;
    
    companion object {
        /**
         * Get NotificationType from string, with fallback to GENERAL
         */
        fun fromString(value: String?): NotificationType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
        
        /**
         * Get FCM topic name for subscription
         */
        fun NotificationType.toTopicName(): String {
            return when (this) {
                BREAKING_NEWS -> "breaking_news"
                SECTION_SPECIFIC -> "section_specific"
                GENERAL -> "general_news"
                SYSTEM -> "system_updates"
            }
        }
        
        /**
         * Get section-specific topic name
         */
        fun getSectionTopic(sectionName: String): String {
            return "section_${sectionName.lowercase()}"
        }
    }
}
