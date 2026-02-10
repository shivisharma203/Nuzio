package com.nuzio.newsapp.features.news.list.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nuzio.newsapp.domain.model.NewsArticle
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enhanced news article card with Material Design 3 styling.
 *
 * Displays article information including image with gradient overlay,
 * source badge, title, description, author, and publication date.
 * Implements elevation animation on press state for tactile feedback.
 *
 * @param article The news article to display
 * @param onClick Callback invoked when the card is clicked
 * @param modifier Optional modifier for customizing the card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedArticleCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSaved: Boolean,
    onBookmarkClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 2.dp,
        label = "card_elevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Article image with gradient overlay and source badge
            ArticleImageSection(
                imageUrl = article.urlToImage,
                sourceName = article.source.name
            )

            // Article content (title, description, metadata)
            ArticleContentSection(
                title = article.title,
                description = article.description,
                author = article.author,
                publishedAt = article.publishedAt
            )
        }
    }

    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

/**
 * Article image section with gradient overlay and source badge.
 *
 * Displays the article's featured image with a gradient overlay at the bottom
 * for text readability. Source name badge positioned at bottom-left with
 * semi-transparent background for visibility against varying image content.
 *
 * @param imageUrl URL of the article's featured image (nullable)
 * @param sourceName Name of the news source
 */
@Composable
private fun ArticleImageSection(
    imageUrl: String?,
    sourceName: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (imageUrl != null) {
            // Article image
            AsyncImage(
                model = imageUrl,
                contentDescription = "Article image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay for better text contrast
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        } else {
            // Placeholder when no image is available
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "No image",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Source badge
        SourceBadge(
            sourceName = sourceName,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )
    }
}

/**
 * Source badge with semi-transparent background.
 *
 * Displays the news source name in a pill-shaped badge with rounded corners
 * and semi-transparent background for visibility on images.
 *
 * @param sourceName Name of the news source
 * @param modifier Optional modifier for positioning
 */
@Composable
private fun SourceBadge(
    sourceName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
    ) {
        Text(
            text = sourceName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Article content section with title, description, and metadata.
 *
 * Displays the article's title, description (if available), author,
 * and publication date in a structured layout following Material Design
 * typography hierarchy.
 *
 * @param title Article headline
 * @param description Article description (nullable)
 * @param author Article author (nullable)
 * @param publishedAt Publication timestamp in ISO format
 */
@Composable
private fun ArticleContentSection(
    title: String,
    description: String?,
    author: String?,
    publishedAt: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Article title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Article description
        if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Metadata row (author and date)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Author
            if (!author.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Author",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = author,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Published date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Published",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatPublishedDate(publishedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Formats ISO 8601 timestamp to relative time string.
 *
 * Converts publication timestamps to human-readable relative time
 * such as "2h ago", "3d ago", "1w ago" for recent articles,
 * or formatted date for older articles.
 *
 * @param isoDate ISO 8601 formatted date string
 * @return Human-readable relative time string
 */
private fun formatPublishedDate(isoDate: String): String {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(isoDate)

        if (date != null) {
            val now = System.currentTimeMillis()
            val diff = now - date.time

            when {
                diff < 60_000 -> "Just now"
                diff < 3600_000 -> "${diff / 60_000}m ago"
                diff < 86400_000 -> "${diff / 3600_000}h ago"
                diff < 604800_000 -> "${diff / 86400_000}d ago"
                diff < 2592000_000 -> "${diff / 604800_000}w ago"
                else -> {
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    outputFormat.format(date)
                }
            }
        } else {
            "Unknown"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}