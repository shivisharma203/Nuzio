package com.nuzio.newsapp.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Animated bookmark button with filled/unfilled states.
 *
 * @param isSaved Whether the article is currently bookmarked
 * @param onClick Callback when bookmark button is clicked
 * @param modifier Optional modifier
 * @param tint Color of the bookmark icon
 */
@Composable
fun BookmarkButton(
    isSaved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    // Animate scale on state change for visual feedback
    val scale by animateFloatAsState(
        targetValue = if (isSaved) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "bookmark_scale_animation"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isSaved) {
                Icons.Filled.Bookmark
            } else {
                Icons.Outlined.BookmarkBorder
            },
            contentDescription = if (isSaved) {
                "Remove bookmark"
            } else {
                "Save article"
            },
            tint = if (isSaved) tint else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(24.dp)
                .scale(scale)
        )
    }
}