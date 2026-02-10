package com.nuzio.newsapp.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nuzio.newsapp.domain.model.NewsArticle

/**
 * Enhanced article card with bookmark functionality.
 */
@Composable
fun EnhancedArticleCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false, // ADD
    onBookmarkClick: (() -> Unit)? = null // ADD
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Article image
            if (article.urlToImage != null) {
                AsyncImage(
                    model = article.urlToImage,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Content with bookmark button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Article content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Source name
                    Text(
                        text = article.source.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Title
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (article.description != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = article.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Author and date
                    Text(
                        text = buildString {
                            if (article.author != null) {
                                append(article.author)
                                append(" • ")
                            }
                            append(article.getRelativeTime())
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bookmark button
                if (onBookmarkClick != null) {
                    BookmarkButton(
                        isSaved = isSaved,
                        onClick = onBookmarkClick,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

// Helper extension for relative time
private fun NewsArticle.getRelativeTime(): String {
    // TODO: Implement proper relative time formatting
    // For now, just return published date
    return publishedAt.take(10) // Returns YYYY-MM-DD
}