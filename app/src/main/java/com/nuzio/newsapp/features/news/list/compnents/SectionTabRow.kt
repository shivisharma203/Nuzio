package com.nuzio.newsapp.features.news.list.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nuzio.newsapp.features.news.list.NewsSection

/**
 * Scrollable tab row displaying news sections with Material Design 3 styling.
 *
 * Implements horizontal scrolling tabs with animated selection indicator,
 * following Material Design 3 specifications for tab navigation. Each tab
 * displays both an icon and label for clear section identification.
 *
 * @param selectedSection The currently selected news section
 * @param onSectionSelected Callback invoked when user taps a section tab
 * @param modifier Optional modifier for customizing the tab row
 */
@Composable
fun SectionTabRow(
    selectedSection: NewsSection,
    onSectionSelected: (NewsSection) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedSection.ordinal,
        modifier = modifier.fillMaxWidth(),
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            if (tabPositions.isNotEmpty() && selectedSection.ordinal < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection.ordinal]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    ) {
        NewsSection.values().forEach { section ->
            SectionTab(
                section = section,
                selected = section == selectedSection,
                onClick = { onSectionSelected(section) }
            )
        }
    }
}

/**
 * Individual section tab with icon and label.
 *
 * Displays animated selection state with color transitions and proper
 * accessibility support through content descriptions.
 *
 * @param section The news section this tab represents
 * @param selected Whether this tab is currently selected
 * @param onClick Callback invoked when tab is tapped
 */
@Composable
private fun SectionTab(
    section: NewsSection,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "tab_color_animation"
    )

    val animatedFontWeight = if (selected) FontWeight.Bold else FontWeight.Normal

    Tab(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = section.icon,
                contentDescription = section.description,
                tint = animatedColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = section.displayName,
                color = animatedColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = animatedFontWeight
            )
        }
    }
}