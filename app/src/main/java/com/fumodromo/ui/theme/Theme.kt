package com.fumodromo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FumoDarkScheme = darkColorScheme(
    primary = Color(0xFFFF6B6B),
    onPrimary = Color(0xFF2A0B0D),
    primaryContainer = Color(0xFF5C1217),
    onPrimaryContainer = Color(0xFFFFDAD9),
    secondary = Color(0xFF8BE9FD),
    onSecondary = Color(0xFF00363E),
    background = Color(0xFF141217),
    onBackground = Color(0xFFECE0E2),
    surface = Color(0xFF211E24),
    onSurface = Color(0xFFECE0E2),
    surfaceVariant = Color(0xFF3A363E),
    onSurfaceVariant = Color(0xFFD4C2C8),
)

private val FumoLightScheme = lightColorScheme(
    primary = Color(0xFFB3261E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = Color(0xFF006878),
    onSecondary = Color.White,
    background = Color(0xFFFFF8F7),
    onBackground = Color(0xFF231F20),
    surface = Color(0xFFFFF8F7),
    onSurface = Color(0xFF231F20),
    surfaceVariant = Color(0xFFF2DEE1),
    onSurfaceVariant = Color(0xFF524345),
)

@Composable
fun FumodromoTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) FumoDarkScheme else FumoLightScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
