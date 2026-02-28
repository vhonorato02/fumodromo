package com.fumodromo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Esquema = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    background = Color(0xFF000000),
    onBackground = Color.White,
    surface = Color(0xFF0A0A0A),
    onSurface = Color.White,
    secondary = Color(0xFFB0B0B0),
)

@Composable
fun FumodromoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Esquema, content = content)
}
