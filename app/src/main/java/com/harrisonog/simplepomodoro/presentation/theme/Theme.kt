package com.harrisonog.simplepomodoro.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

val FocusColor = Color(0xFF6B9BD1)      // Calm blue
val BreakColor = Color(0xFF98C9A3)      // Gentle green
val BackgroundColor = Color(0xFF1A1A1A) // Dark but not harsh
val TextColor = Color(0xFFE0E0E0)       // Soft white

private val WearColorPalette = Colors(
    primary = FocusColor,
    primaryVariant = Color(0xFF5A8AC0),
    secondary = BreakColor,
    secondaryVariant = Color(0xFF87B892),
    background = BackgroundColor,
    surface = Color(0xFF2A2A2A),
    error = Color(0xFFCF6679),
    onPrimary = TextColor,
    onSecondary = TextColor,
    onBackground = TextColor,
    onSurface = TextColor,
    onError = Color.Black
)

@Composable
fun SimplePomodoroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = WearColorPalette,
        content = content
    )
}