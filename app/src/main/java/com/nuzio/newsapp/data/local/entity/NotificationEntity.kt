package com.nuzio.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nuzio.newsapp.domain.model.NotificationData
import com.nuzio.newsapp.domain.model.NotificationType

/**
 * Room entity representing a notification in the local database
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val articleUrl: String?,
    val imageUrl: String?,
    val section: String?,
    val timestamp: Long,
    val isRead: Boolean,
    val dataJson: String // JSON string of additional data
)

/**
 * Extension function to convert NotificationEntity to domain model
 */
fun NotificationEntity.toDomain(): NotificationData {
    // Parse JSON string back to map (simple implementation)
    val dataMap = if (dataJson.isNotBlank()) {
        try {
            dataJson
                .removeSurrounding("{", "}")
                .split(",")
                .associate {
                    val (key, value) = it.split(":")
                    key.trim().removeSurrounding("\"") to value.trim().removeSurrounding("\"")
                }
        } catch (e: Exception) {
            emptyMap()
        }
    } else {
        emptyMap()
    }

    return NotificationData(
        id = id,
        title = title,
        message = message,
        type = NotificationType.fromString(type),
        articleUrl = articleUrl,
        imageUrl = imageUrl,
        section = section,
        timestamp = timestamp,
        isRead = isRead,
        data = dataMap
    )
}

/**
 * Extension function to convert domain model to NotificationEntity
 */
fun NotificationData.toEntity(): NotificationEntity {
    // Simple JSON serialization for data map
    val dataJson = if (data.isNotEmpty()) {
        data.entries.joinToString(",", "{", "}") { (key, value) ->
            "\"$key\":\"$value\""
        }
    } else {
        ""
    }

    return NotificationEntity(
        id = id,
        title = title,
        message = message,
        type = type.name,
        articleUrl = articleUrl,
        imageUrl = imageUrl,
        section = section,
        timestamp = timestamp,
        isRead = isRead,
        dataJson = dataJson
    )
}

/**
 * Extension function to convert list of entities to domain models
 */
fun List<NotificationEntity>.toDomain(): List<NotificationData> {
    return map { it.toDomain() }
}
