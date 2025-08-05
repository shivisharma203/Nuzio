package com.nuzio.newsapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
val BohoPrimary = Color(0xFFC18E5F)
val BohoSecondary = Color(0xFF8C6239)
val BohoBackground = Color(0xFFF9F4E7)
val BohoSurface = Color(0xFFE9D8A6)
val BohoOnPrimary = Color(0xFFFFFFFF)
val BohoOnSecondary = Color(0xFFFFFFFF)
private val DarkColorScheme = darkColorScheme(
    primary = BohoPrimary,
    secondary = BohoSecondary,
    background = Color.Black,
    surface = Color.DarkGray,
    onPrimary = BohoOnPrimary,
    onSecondary = BohoOnSecondary,
)

private val LightColorScheme = lightColorScheme(
    primary = BohoPrimary,
    secondary = BohoSecondary,
    background = BohoBackground,
    surface = BohoSurface,
    onPrimary = BohoOnPrimary,
    onSecondary = BohoOnSecondary,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun NuzioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}